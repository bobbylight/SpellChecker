/*
 * Jazzy - a Java library for Spell Checking
 * Copyright (C) 2001 Mindaugas Idzelis
 * Full text of license can be found in LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.fife.com.swabunga.spell.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * A dictionary held in memory, read from word lists with one word per line.
 * There are many open source word lists; for a few, see
 * <a href="http://wordlist.sourceforge.net/">http://wordlist.sourceforge.net/</a>.
 * <p>
 * To suggest corrections, words are grouped by their Double Metaphone codes.
 * A word is in the group for its primary code, and also in the group for its
 * alternate code if that's different, so words are suggested for misspellings
 * that match either pronunciation.
 * <p>
 * Words added with {@link #addWord(String)} are only saved if the dictionary
 * was created from a word list file.
 */
public class SpellDictionaryHashMap implements SpellDictionary {

  /**
   * The initial capacity of the maps holding the words.
   */
  private static final int INITIAL_CAPACITY = 16 * 1024;

  private final DoubleMeta doubleMeta = new DoubleMeta();

  /**
   * Words, grouped by phonetic code. Each word is in the group for each of
   * its (one or two) codes.
   */
  private final Map<String, List<String>> mainDictionary = new HashMap<>(INITIAL_CAPACITY);

  /**
   * Every word in {@link #mainDictionary}, for exact lookups in
   * {@link #isCorrect(String)} without computing a phonetic code.
   */
  private final Set<String> words = new HashSet<>(INITIAL_CAPACITY);

  /** Holds the dictionary file for appending. */
  private File dictFile;

  /**
   * Creates an empty dictionary.
   *
   * @see #addWord(String)
   */
  public SpellDictionaryHashMap() {
  }

  /**
   * Creates a dictionary from a word list.<p>
   * Note that since there's no actual file for the word list, words added
   * via {@link #addWord(String)} will not persist beyond the application's
   * lifecycle.
   *
   * @param wordList The word list, one word per line. This is not closed.
   * @throws IOException If an IO error occurs.
   */
  public SpellDictionaryHashMap(Reader wordList) throws IOException {
    readWords(new BufferedReader(wordList), this::putWord);
  }

  /**
   * Creates a dictionary from a word list file. The word list is read, and
   * words added via {@link #addWord(String)} are appended to it, as UTF-8.
   *
   * @param wordList The word list file, one word per line.
   * @throws IOException If an IO error occurs.
   */
  public SpellDictionaryHashMap(File wordList) throws IOException {
    dictFile = wordList;
    try (BufferedReader in = new BufferedReader(new FileReader(wordList, StandardCharsets.UTF_8))) {
      readWords(in, this::putWord);
    }
  }

  /**
   * Adds the words in a word list to this dictionary. This can be called as
   * many times as needed. Words already in the dictionary, ignoring case,
   * aren't added again.
   * <p>
   * The words aren't added to this dictionary's file, if it has one.
   *
   * @param wordList The word list, one word per line. This is not closed.
   * @throws IOException If an IO error occurs.
   */
  public void addDictionary(Reader wordList) throws IOException {
    readWords(new BufferedReader(wordList), this::putWordUnique);
  }

  /**
   * Adds a word to this dictionary, and to its file if it has one.
   *
   * @param word The word to add.
   * @return Whether the word was added to the dictionary's file. This is
   *         {@code false} if it doesn't have one, or an IO error occurred.
   */
  @Override
  public boolean addWord(String word) {
    putWord(word);
    if (dictFile != null) {
      try (BufferedWriter w = new BufferedWriter(new FileWriter(dictFile, StandardCharsets.UTF_8, true))) {
        w.write(word);
        w.write("\n");
        return true;
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Passes each non-empty line of a word list to a method that adds it to
   * the dictionary. The reader is not closed.
   */
  private static void readWords(BufferedReader in, Consumer<String> putWord) throws IOException {
    String line;
    while ((line = in.readLine()) != null) {
      if (!line.isEmpty()) {
        putWord.accept(line);
      }
    }
  }

  /**
   * Adds a word to the dictionary. Assumes the word isn't already in this
   * dictionary, as it can create duplicates (which should be harmless, but
   * not performant).
   *
   * @param word The word to add.
   * @see #putWordUnique(String)
   */
  void putWord(String word) {
    for (String code : doubleMeta.getCodes(word)) {
      mainDictionary.computeIfAbsent(code, k -> new ArrayList<>()).add(word);
    }
    words.add(word);
  }

  /**
   * Adds a word, if it is not already present in the dictionary. A word
   * with a different case is considered the same.
   *
   * @param word The word to add.
   * @see #putWord(String)
   */
  void putWordUnique(String word) {
    // A word is in the group for its primary code (and for its alternate
    // code too, if any), so checking that group is enough
    List<String> codes = doubleMeta.getCodes(word);
    List<String> primaryGroup = mainDictionary.get(codes.get(0));
    if (primaryGroup == null || primaryGroup.stream().noneMatch(word::equalsIgnoreCase)) {
      putWord(word);
    }
  }

  /**
   * Returns true if the word, or its lower-case form, is in this dictionary.
   */
  @Override
  public boolean isCorrect(String word) {
    return words.contains(word) || words.contains(word.toLowerCase(Locale.ROOT));
  }

  /**
   * Returns a list of Word objects that are the suggestions to an
   * incorrect word.
   *
   * @param word Suggestions for given misspelled word
   * @param config The configuration providing the score threshold and edit costs
   * @return a List of suggestions
   */
  @Override
  public List<Word> getSuggestions(String word, Configuration config) {

    int threshold = config.getInteger(Configuration.SPELL_THRESHOLD);
    EditDistance editDistance = new EditDistance(config);

    // A word can be in two groups, so track which have been suggested
    Set<String> suggested = new HashSet<>();

    // add all words that sound the same, by either of the word's codes
    List<String> codes = doubleMeta.getCodes(word);
    List<Word> phoneticList = getWordsFromCodes(word, codes, editDistance, threshold, suggested);

    // do some transformations to pick up more results. These use primary
    // codes only, since there are hundreds of them per word.
    Set<String> nearMissCodes = new HashSet<>();
    char[] replaceList = DoubleMeta.CODE_CHARS;
    addSwapCodes(word, nearMissCodes);
    addReplacementCodes(word, replaceList, nearMissCodes);
    addInsertionCodes(word, replaceList, nearMissCodes);
    addDeletionCodes(word, nearMissCodes);

    codes.forEach(nearMissCodes::remove); //already accounted for in phoneticList

    List<Word> wordlist = getWordsFromCodes(word, nearMissCodes, editDistance, threshold, suggested);

    if (wordlist.isEmpty() && phoneticList.isEmpty())
      addBestGuess(word, codes, phoneticList, editDistance);


    // We sort a List at the end instead of maintaining a
    // continuously sorted TreeSet because every time you add a collection
    // to a tree set it has to be resorted. It's better to do this operation
    // once at the end.

    phoneticList.sort(Word.COST_COMPARATOR); //always sort phonetic matches along the top
    wordlist.sort(Word.COST_COMPARATOR); //the non-phonetic matches can be listed below

    phoneticList.addAll(wordlist);
    return phoneticList;
  }

  /**
   * Adds the phonetic codes of every variant of {@code word} with two
   * adjacent characters swapped.
   */
  private void addSwapCodes(String word, Set<String> codes) {
    char[] chars = word.toCharArray();
    for (int i = 0; i < chars.length - 1; i++) {
      swap(chars, i, i + 1);
      codes.add(getCode(new String(chars)));
      swap(chars, i, i + 1);
    }
  }

  /**
   * Adds the phonetic codes of every variant of {@code word} with one
   * character replaced by a character from {@code replaceList}.
   */
  private void addReplacementCodes(String word, char[] replaceList, Set<String> codes) {
    char[] chars = word.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char original = chars[i];
      for (char c : replaceList) {
        chars[i] = c;
        codes.add(getCode(new String(chars)));
      }
      chars[i] = original;
    }
  }

  /**
   * Adds the phonetic codes of every variant of {@code word} with a character
   * from {@code replaceList} inserted at any position, working from the end
   * of the word to the start.
   */
  private void addInsertionCodes(String word, char[] replaceList, Set<String> codes) {
    char[] chars = (word + ' ').toCharArray();
    for (int i = chars.length - 1; ; i--) {
      for (char c : replaceList) {
        chars[i] = c;
        codes.add(getCode(new String(chars)));
      }
      if (i == 0) {
        break;
      }
      chars[i] = chars[i - 1]; // shift the insertion point one to the left
    }
  }

  /**
   * Adds the phonetic codes of every variant of {@code word} with one
   * character deleted, working from the end of the word to the start.
   */
  private void addDeletionCodes(String word, Set<String> codes) {
    char[] chars = word.toCharArray();
    char[] deleted = new char[chars.length - 1];
    System.arraycopy(chars, 0, deleted, 0, deleted.length);

    char a = chars[chars.length - 1];
    int i = deleted.length;
    while (true) {
      codes.add(getCode(new String(deleted)));
      if (i == 0) {
        break;
      }
      char b = a;
      a = deleted[i - 1];
      deleted[i - 1] = b;
      --i;
    }
  }

  private static void swap(char[] chars, int i, int j) {
    char temp = chars[i];
    chars[i] = chars[j];
    chars[j] = temp;
  }

  private void addBestGuess(String word, List<String> codes, List<Word> wordList, EditDistance editDistance) {
    if (!wordList.isEmpty())
      throw new IllegalArgumentException("the wordList must be empty");

    int bestScore = Integer.MAX_VALUE;

    // Guess from words matching either code. Words that only match by an
    // alternate code matter here: "speshal" is too far from "special" by
    // edit distance to be suggested otherwise.
    Set<String> simwords = new LinkedHashSet<>();
    for (String code : codes) {
      simwords.addAll(getWords(code)); // A word can be in the groups for both codes
    }

    List<Word> candidates = new ArrayList<>();

    for (String similar : simwords) {
      int distance = editDistance.getDistance(word, similar);
      if (distance <= bestScore) {
        bestScore = distance;
        candidates.add(new Word(similar, distance));
      }
    }

    //now, only pull out the guesses that had the best score
    for (Word candidate : candidates) {
      if (candidate.getCost() == bestScore)
        wordList.add(candidate);
    }

  }

  /**
   * Returns the words in the groups for some codes that are within the
   * threshold edit distance of a word, skipping any already suggested.
   *
   * @param suggested The words already suggested. Words returned are added
   *        to it.
   */
  private List<Word> getWordsFromCodes(String word, Collection<String> codes,
                                       EditDistance editDistance, int threshold, Set<String> suggested) {
    List<Word> result = new ArrayList<>();

    for (String code : codes) {
      for (String similar : getWords(code)) {
        if (!suggested.contains(similar)) {
          int distance = editDistance.getDistance(word, similar);
          if (distance < threshold) {
            result.add(new Word(similar, distance));
            suggested.add(similar);
          }
        }
      }
    }
    return result;
  }

  /**
   * Returns a word's primary phonetic code.
   *
   * @param word The word.
   * @return The primary code.
   */
  String getCode(String word) {
    return doubleMeta.getPrimaryCode(word);
  }

  /**
   * Returns the words in the group for a phonetic code.
   *
   * @param code The code.
   * @return The words. This may be empty, but is never {@code null}.
   */
  List<String> getWords(String code) {
    List<String> result = mainDictionary.get(code);
    return result == null ? Collections.emptyList() : Collections.unmodifiableList(result);
  }
}
