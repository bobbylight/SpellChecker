/*
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
/* Created by bgalbs on Jan 30, 2003 at 11:45:25 PM */
package org.fife.com.swabunga.spell.engine;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Container for various methods that any <code>SpellDictionary</code> will use.
 * This class is based on the original Jazzy aspell port.
 * <p/>
 * Derived classes will need words list files as spell checking reference.
 * Words list file is a dictionary with one word per line. There are many
 * open source dictionary files, see:
 * <a href="http://wordlist.sourceforge.net/">
 * http://wordlist.sourceforge.net/</a>
 * <p/>
 * You can choose words lists form <a href="http://aspell.net/">aspell</a>
 * many differents languages dictionaries. To grab some, install
 * <code>aspell</code> and the dictionaries you require. Then run aspell
 * specifying the name of the dictionary and the words list file to dump it
 * into, for example:
 * <pre>
 * aspell --master=fr-40 dump master &gt; fr-40.txt
 * </pre>
 * Note: the number following the language is the size indicator. A bigger
 * number gives a more extensive language coverage. Size 40 is more than
 * adequate for many usages.
 * <p/>
 * For some languages, Aspell can also supply you with the phonetic file.
 * On Windows, go into aspell <code>data</code> directory and copy the
 * phonetic file corresponding to your language, for example the
 * <code>fr_phonet.dat</code> for the <code>fr</code> language. The phonetic
 * file should be in directory <code>/usr/share/aspell</code> on Unix.
 *
 * @see GenericTransformator GenericTransformator for information on
 *      phonetic files.
 */
public abstract class SpellDictionaryASpell implements SpellDictionary {


  /** The reference to a Transformator, used to transform a word into it's phonetic code. */
  private final Transformator tf;

  /**
   * Constructs a new SpellDictionaryASpell that uses {@link DoubleMeta}
   * transformation.
   */
  protected SpellDictionaryASpell() {
    tf = new DoubleMeta();
  }

  /**
   * Constructs a new SpellDictionaryASpell.
   *
   * @param phonetic The file to use for phonetic transformation of the
   *        words list. If <code>phonetic</code> is null, the transformation
   *        uses {@link DoubleMeta} transformation.
   * @throws IOException  indicates problems reading the phonetic information.
   */
  public SpellDictionaryASpell(File phonetic) throws IOException {
    tf = phonetic == null ? new DoubleMeta() : new GenericTransformator(phonetic);
  }

  /**
   * Constructs a new SpellDictionaryASpell.
   *
   * @param phonetic The file to use for phonetic transformation of the
   *        words list. If <code>phonetic</code> is null, the transformation
   *        uses {@link DoubleMeta} transformation.
   * @param encoding Uses the character set encoding specified
   * @throws IOException  indicates problems reading the phonetic information.
   */
  public SpellDictionaryASpell(File phonetic, String encoding) throws IOException {
    tf = phonetic == null ? new DoubleMeta() : new GenericTransformator(phonetic, encoding);
  }

  /**
   * Constructs a new SpellDictionaryASpell.
   *
   * @param phonetic The Reader to use for phonetic transformation of the
   *        words list. If <code>phonetic</code> is null, the transformation
   *        uses {@link DoubleMeta} transformation.
   * @throws IOException  indicates problems reading the phonetic information.
   */
  public SpellDictionaryASpell(Reader phonetic) throws IOException {
    tf = phonetic == null ? new DoubleMeta() : new GenericTransformator(phonetic);
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

    String code = getCode(word);

    // add all words that have the same phonetics
    List<Word> phoneticList = getWordsFromCode(word, Set.of(code), editDistance, threshold);

    // do some transformations to pick up more results
    Set<String> nearMissCodes = new HashSet<>();
    char[] replaceList = tf.getReplaceList();
    addSwapCodes(word, nearMissCodes);
    addReplacementCodes(word, replaceList, nearMissCodes);
    addInsertionCodes(word, replaceList, nearMissCodes);
    addDeletionCodes(word, nearMissCodes);

    nearMissCodes.remove(code); //already accounted for in phoneticList

    List<Word> wordlist = getWordsFromCode(word, nearMissCodes, editDistance, threshold);

    if (wordlist.isEmpty() && phoneticList.isEmpty())
      addBestGuess(word, phoneticList, editDistance);


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

  /**
   * When we don't come up with any suggestions (probably because the threshold was too strict),
   * then pick the best guesses from the those words that have the same phonetic code.
   *
   * @param word - the word we are trying to spell correct
   * @param wordList - the linked list that will get the best guess
   * @param editDistance - computes the distance between words
   */
  private void addBestGuess(String word, List<Word> wordList, EditDistance editDistance) {
    if (!wordList.isEmpty())
      throw new IllegalArgumentException("the wordList must be empty");

    int bestScore = Integer.MAX_VALUE;

    String code = getCode(word);
    List<String> simwordlist = getWords(code);

    List<Word> candidates = new ArrayList<>();

    for (String similar : simwordlist) {
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

  private List<Word> getWordsFromCode(String word, Set<String> codes,
                                      EditDistance editDistance, int threshold) {
    List<Word> result = new ArrayList<>();

    for (String code : codes) {
      List<String> simwordlist = getWords(code);
      for (String similar : simwordlist) {
        int distance = editDistance.getDistance(word, similar);
        if (distance < threshold) {
          result.add(new Word(similar, distance));
        }
      }
    }
    return result;
  }

  /**
   * Returns the phonetic code representing the word.
   *
   * @param word The word we want the phonetic code.
   * @return The value of the phonetic code for the word.
   */
  public String getCode(String word) {
    return tf.transform(word);
  }

  /**
   * Returns a list of words that have the same phonetic code.
   *
   * @param phoneticCode The phonetic code common to the list of words
   * @return A list of words having the same phonetic code
   */
  protected abstract List<String> getWords(String phoneticCode);

  /**
   * Returns true if the word is correctly spelled against the current word list.
   */
  @Override
public boolean isCorrect(String word) {
    List<String> possible = getWords(getCode(word));
    if (possible.contains(word))
      return true;
    //JMH should we always try the lowercase version. If I don't then
    // capitalized words are always returned as incorrect.
    return possible.contains(word.toLowerCase(Locale.ROOT));
  }
}
