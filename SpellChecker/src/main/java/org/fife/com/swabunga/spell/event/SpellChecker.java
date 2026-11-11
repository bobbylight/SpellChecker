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
package org.fife.com.swabunga.spell.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.fife.com.swabunga.spell.engine.Configuration;
import org.fife.com.swabunga.spell.engine.SpellDictionary;
import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.engine.Word;


/**
 * This is the main class for spell checking (using the new event based spell
 * checking).
 * <p/>
 * By default, the class makes a user dictionary to accumulate added words.
 * Since this user directory has no file assign to persist added words, they
 * will be retained for the duration of the spell checker instance.
 * If you set a user dictionary like
 * {@link org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap SpellDictionaryHashMap}
 * to persist the added word, the user dictionary will have the possibility to
 * grow and be available across different invocations of the spell checker.
 *
 * @author Jason Height (jheight@chariot.net.au) 19 June 2002
 */
public class SpellChecker {

  private List<SpellCheckListener> eventListeners = new ArrayList<>();
  private List<SpellDictionary> dictionaries = new ArrayList<>();
  private SpellDictionary userDictionary = new SpellDictionaryHashMap();

  private final Configuration config;

  /**
   * This variable holds all the words that are to be always ignored.
   */
  private Set<String> ignoredWords = new HashSet<>();

  // added caching - bd
  // For cached operation a separate user dictionary is required
  private Map<String, List<Word>> cache;
  private int threshold;
  private int cacheSize;

  /**
   * Constructs the SpellChecker with the default configuration, as returned
   * by {@link Configuration#getConfiguration()}.
   *
   * @param dictionary The dictionary used for looking up words.
   * @see #SpellChecker(SpellDictionary, Configuration)
   */
  public SpellChecker(SpellDictionary dictionary) {
    this(dictionary, Configuration.getConfiguration());
  }

  /**
   * Constructs the SpellChecker with a specific configuration.
   *
   * @param dictionary The dictionary used for looking up words.
   * @param config The configuration holding the spell checking engine
   *        parameters. This cannot be {@code null}.
   * @see #SpellChecker(SpellDictionary)
   */
  public SpellChecker(SpellDictionary dictionary, Configuration config) {
    this.config = Objects.requireNonNull(config, "config must be non-null");
    addDictionary(dictionary);
  }

  /**
   * Accumulates a dictionary at the end of the dictionaries list used
   * for looking up words. Adding a dictionary give the flexibility to
   * assign the base language dictionary, then a more technical, then...
   *
   * @param dictionary the dictionary to add at the end of the dictionary list.
   */
  public void addDictionary(SpellDictionary dictionary) {
    this.dictionaries.add(Objects.requireNonNull(dictionary, "dictionary must be non-null"));
  }

  /**
   * Registers the user dictionary to which words are added.
   *
   * @param dictionary the dictionary to use when the user specify a new word
   *        to add. This cannot be {@code null}.
   */
  public void setUserDictionary(SpellDictionary dictionary) {
    userDictionary = Objects.requireNonNull(dictionary, "dictionary must be non-null");
  }

  /**
   * Supply the instance of the configuration holding the spell checking engine
   * parameters.
   *
   * @return Current Configuration
   */
  public Configuration getConfiguration() {
    return config;
  }

  /**
   * Adds a SpellCheckListener to the listeners list.
   *
   * @param listener The feature to be added to the SpellCheckListener attribute
   */
  public void addSpellCheckListener(SpellCheckListener listener) {
    eventListeners.add(listener);
  }


  /**
   * Removes a SpellCheckListener from the listeners list.
   *
   * @param listener The listener to be removed from the listeners list.
   */
  public void removeSpellCheckListener(SpellCheckListener listener) {
    eventListeners.remove(listener);
  }


  /**
   * This method clears the words that are currently being remembered as
   * <code>Ignore All</code> words and <code>Replace All</code> words.
   */
  public void reset() {
    ignoredWords.clear();
  }


  /**
   * Verifies if the word that is being spell checked contains at least a
   * digit.
   * Returns true if this word contains a digit.
   *
   * @param word The word to analyze for digit.
   * @return true if the word contains at least a digit.
   */
  private static boolean isDigitWord(CharSequence word) {
    return word.codePoints().anyMatch(Character::isDigit);
  }


  /**
   * Returns whether the text at an offset starts like an Internet address,
   * that is, with one of these prefixes (case-insensitively):
   * <ul>
   * <li>http://</li>
   * <li>ftp://</li>
   * <li>https://</li>
   * <li>ftps://</li>
   * <li>www.</li>
   * </ul>
   * <p>
   * This doesn't recognize email addresses. The text may be the rest of
   * the document (or line), so it isn't scanned for an {@code @}.
   * The prefixes are compared in place, without copying the text.
   *
   * @param text The text.
   * @param offset The offset in {@code text} to check.
   * @return Whether the text at {@code offset} starts like an Internet address.
   * @see #isINETWord(String)
   */
  static boolean beginsAsINETWord(String text, int offset) {
    // robert: In standard Jazzy distributions, this is "isINETWord(String)".
    // Prefixes are compared in place, and checked roughly in order of how
    // commonly they're seen.
    return startsWithIgnoreCase(text, offset, "https://") ||
        startsWithIgnoreCase(text, offset, "www.") ||
        startsWithIgnoreCase(text, offset, "http://") ||
        startsWithIgnoreCase(text, offset, "ftp://") ||
        startsWithIgnoreCase(text, offset, "ftps://");
  }


  private static boolean startsWithIgnoreCase(String str, int offset, String prefix) {
    return str.regionMatches(true, offset, prefix, 0, prefix.length());
  }


  /**
   * Checks if the word that is being spell checked contains an Internet
   * address. This method look for typical protocol or the habitual string
   * in the word:
   * <ul>
   * <li>http://</li>
   * <li>ftp://</li>
   * <li>https://</li>
   * <li>ftps://</li>
   * <li>www.</li>
   * <li>anything@anythingelse</li>
   * </ul>
   * <p>
   * It is assumed that <code>word</code> is just the word to scan, without
   * any trailing characters.  This is different from the standard Jazzy
   * distribution's implementation (which has been renamed to
   * <code>beginsAsINETWord(String, int)</code>).
   *
   * @param word The word to analyze for an Internet address.
   * @return true if this word looks like an Internet address.
   * @see #beginsAsINETWord(String, int)
   */
  public static boolean isINETWord(String word) {
    return beginsAsINETWord(word, 0) || word.indexOf('@') > 0;
  }


  /**
   * Verifies if the word that is being spell checked contains all
   * upper-cased characters.
   *
   * @param word The word to analyze for upper-cases characters
   * @return true if this word contains all upper case characters
   */
  private static boolean isUpperCaseWord(CharSequence word) {
    return word.codePoints().noneMatch(Character::isLowerCase);
  }


  /**
   * Verifies if the word that is being spell checked contains lower and
   * upper-cased characters. A word whose only upper-cased character is the
   * first one, such as "Hello", is not considered a mixed case word, whether
   * or not it starts a sentence.
   *
   * @param word       The word to analyze for mixed cases characters
   * @return true if this word contains mixed case characters
   */
  private static boolean isMixedCaseWord(CharSequence word) {
    int strLen = word.length();
    boolean isUpper = Character.isUpperCase(word.charAt(0));
    //Ignore the first character if it was upper-cased, since a capitalized
    //word (sentence start, proper noun, title) is normal
    if (isUpper && (strLen > 1))
      isUpper = Character.isUpperCase(word.charAt(1));
    if (isUpper) {
      for (int i = word.length() - 1; i > 0; i--) {
        if (Character.isLowerCase(word.charAt(i))) {
          return true;
        }
      }
    }
    else {
      for (int i = word.length() - 1; i > 0; i--) {
        if (Character.isUpperCase(word.charAt(i))) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * This method will fire the spell check event and then handle the event
   * action that has been selected by the user.
   *
   * @param event   The event to handle
   * @return Returns true if the event action is to cancel the current spell checking, false if
   *         the spell checking should continue
   */
  protected boolean fireAndHandleEvent(SpellCheckEvent event) {
     boolean cancel = false;
     for (int i = eventListeners.size() - 1; i >= 0; i--) {
       cancel |= eventListeners.get(i).spellingError(event);
     }
    return cancel;
  }

  /**
   * Adds a word to the list of ignored words.
   *
   * @param word The text of the word to ignore
   */
  public void ignoreAll(String word) {
    ignoredWords.add(word);
  }

  /**
   * Adds a word to the user dictionary.
   *
   * @param word The text of the word to add
   * @return Whether the word was successfully added
   */
  public boolean addToDictionary(String word) {
    if (!userDictionary.isCorrect(word)) {
      return userDictionary.addWord(word);
    }
    return false;
  }

  /**
   * Indicates if a word is in the list of ignored words.
   *
   * @param word The text of the word check
   * @return Whether the word is ignored.
   */
  protected boolean isIgnored(String word) {
    return ignoredWords.contains(word);
  }

  /**
   * Verifies if the word to analyze is contained in dictionaries. The order
   * of dictionary lookup is:
   * <ul>
   * <li>The dictionary specified at construction time.</li>
   * <li>Any dictionaries, in the order they were added through
   * {@link SpellChecker#addDictionary}.</li>
   * <li>The default user dictionary, or the one set through
   * {@link SpellChecker#setUserDictionary}.</li>
   * </ul>
   *
   * @param word The word to verify that it's spelling is known.
   * @return true if the word is in a dictionary.
   */
  protected boolean isCorrect(String word) {
    for (SpellDictionary dictionary : dictionaries) {
      if (dictionary.isCorrect(word)) return true;
    }
    return userDictionary.isCorrect(word);
  }

  /**
   * Produces a list of suggested word after looking for suggestions in various
   * dictionaries. The dictionary specified in the constructor is checked first,
   * then any other dictionaries added, and finally the user dictionary.
   * <p>
   * The score threshold and edit costs come from this spell checker's
   * {@link #getConfiguration() configuration}.
   *
   * @param word    The word for which we want to gather suggestions
   * @return the list of words suggested
   * @see Configuration#SPELL_THRESHOLD
   */
  public List<Word> getSuggestions(String word) {
    //long start = System.currentTimeMillis();
    int threshold = config.getInteger(Configuration.SPELL_THRESHOLD);
    if (this.threshold != threshold && cache != null) {
      this.threshold = threshold;
      cache.clear();
    }

    // The cache only holds suggestions from the main dictionaries. It is
    // never handed out, so neither callers nor user dictionary suggestions
    // can change it.
    List<Word> dictionarySuggestions = null;

    if (cache != null) {
        dictionarySuggestions = cache.get(word);
    }

    if (dictionarySuggestions == null) {
      dictionarySuggestions = new ArrayList<>();

      for (SpellDictionary dictionary : dictionaries) {
        if (dictionary != userDictionary)
          addAllNoDuplicates(dictionarySuggestions, dictionary.getSuggestions(word, config));
      }

      if (cache != null && cache.size() < cacheSize)
        cache.put(word, dictionarySuggestions);
    }

    List<Word> suggestions = new ArrayList<>(dictionarySuggestions);
    addAllNoDuplicates(suggestions, userDictionary.getSuggestions(word, config));

    //long time = System.currentTimeMillis() - start;
    //float secs = time/1000f;
    //System.out.println("[DEBUG]: Suggestions for '" + word + "' took " + secs + " seconds");
    return suggestions;
  }

  /**
   * Appends the words in {@code src} to {@code dest}, skipping any already
   * in {@code dest} (or appearing earlier in {@code src}).
   */
  private static void addAllNoDuplicates(List<Word> dest, List<Word> src) {
    Set<Word> present = new HashSet<>(dest);
    for (Word word : src) {
      if (present.add(word)) {
        dest.add(word);
      }
    }
  }

  /**
   * Activates a cache with specified size.
   *
   * @param size - max. number of cache entries (0 to disable cache)
   */
  public void setCacheSize(int size) {
    cacheSize = size;
    if (size == 0)
      cache = null;
    else
      cache = new HashMap<>((size + 2) / 3 * 4);
  }

  /**
   * Does something.
   *
   * @param mixedCaseWord The word to split.
   * @return The split result.
   */
  public static List<String> splitMixedCaseWord(String mixedCaseWord) {

    List<String> parts = new ArrayList<>();

    int offs = 0;
    int adjacentCaps = 0;

    for (int i = 0; i < mixedCaseWord.length(); i++) {

      char ch = mixedCaseWord.charAt(i);

      if (i == 0) {
        adjacentCaps = Character.isUpperCase(ch) ? 1 : 0;
      }
      else {
        if (Character.isUpperCase(ch)) {

          if (adjacentCaps == 0) {
            parts.add(mixedCaseWord.substring(offs, i));
            offs = i;
          }

          adjacentCaps++;
        }
        else if (adjacentCaps > 1) {
          parts.add(mixedCaseWord.substring(offs, i - 1));
          offs = i - 1;
          adjacentCaps = 0;
        }
      }
    }

    parts.add(mixedCaseWord.substring(offs));
    return parts;
  }

  /**
   * This method is called to check the spelling of the words that are returned
   * by the WordTokenizer.
   * <p/>
   * For each invalid word the action listeners will be informed with a new
   * SpellCheckEvent.<p>
   *
   * @param tokenizer The media containing the text to analyze.
   * @return The result of the spell check, including the number of errors
   *         found and whether a listener cancelled it.
   */
  public final SpellCheckResult checkSpelling(WordTokenizer tokenizer) {
    CheckState state = new CheckState(config);
    while (tokenizer.hasMoreWords() && !state.terminated) {
      String word = tokenizer.nextWord();
      checkWord(word, tokenizer.getCurrentWordPosition(), tokenizer.isNewSentence(), state);
    }
    return new SpellCheckResult(state.errors, state.terminated);
  }

  /**
   * Checks the spelling of a single word, firing an event for each error found.
   *
   * @param word The word to check.
   * @param wordStart The offset of the word in the text being checked.
   * @param isNewSentence Whether the word starts a sentence.
   * @param state Tracks the error count and whether a listener cancelled
   *        the spell check.
   */
  private void checkWord(String word, int wordStart, boolean isNewSentence, CheckState state) {
    //Check the spelling of the word
    if (!isCorrect(word)) {

      // Hyphenated compounds such as "well-known" are rarely in the
      // dictionary, so check each part as a word of its own
      if (word.indexOf('-') > -1) {
        checkHyphenatedWordParts(word, wordStart, isNewSentence, state);
        return;
      }

      boolean isMixedCaseWord = isMixedCaseWord(word);

      // robert: If this is a mixed-case word, check spelling of each part
      if (isMixedCaseWord && state.get(CheckState.ANALYZE_CAMEL_CASE)) {

        List<String> parts = splitMixedCaseWord(word);

        int offs = 0;
        for (String part : parts) {

          // Ignore mixed-case word parts, if necessary
          if (!state.get(CheckState.IGNORE_UPPER_CASE) || !isUpperCaseWord(part)) {
            if (!isPartCorrectOrIgnored(part)) {
              reportError(part, wordStart + offs, state);
              if (state.terminated) {
                return;
              }
            }
          }

          offs += part.length();
        }
      }
      else if ((isMixedCaseWord && state.get(CheckState.IGNORE_MIXED_CASE)) ||
          (state.get(CheckState.IGNORE_UPPER_CASE) && isUpperCaseWord(word)) ||
          (word.length() == 1 && state.get(CheckState.IGNORE_SINGLE_LETTERS)) ||
          (state.get(CheckState.IGNORE_DIGIT_WORDS) && isDigitWord(word)) ||
          (state.get(CheckState.IGNORE_INTERNET_ADDRESSES) && isINETWord(word))) {
        //Null event. Since we are ignoring this word due
        //to one of the above cases.
      }
      else {
        //We can't ignore this misspelled word
        //For this invalid word are we ignoring the misspelling?
        if (!isIgnored(word)) {
          reportError(word, wordStart, state);
        }
      }
    }
    else {
      //This is a correctly spelled word. However, perform some extra checks
      //Check for capitalization
      if (isNewSentence && Character.isLowerCase(word.charAt(0)) &&
          !state.get(CheckState.IGNORE_SENTENCE_CAPITALIZATION)) {
        reportError(word, wordStart, state);
      }
    }
  }

  /**
   * Returns whether a part of a camelCase word is spelled correctly or
   * ignored. The part is checked lowercased, so the "Item" in "getItem"
   * matches the dictionary word "item", and also as written, since that's
   * what the tool tip's "Ignore" and "Add" options store.
   */
  private boolean isPartCorrectOrIgnored(String part) {
    String partLower = part.toLowerCase(Locale.ROOT);
    if (isCorrect(partLower) || isIgnored(partLower)) {
      return true;
    }
    return !part.equals(partLower) && (isCorrect(part) || isIgnored(part));
  }

  /**
   * Checks each hyphen-separated part of a word as a word of its own.
   * Only the first part can start a sentence.
   */
  private void checkHyphenatedWordParts(String word, int wordStart, boolean isNewSentence,
                                        CheckState state) {
    int partStart = 0;
    while (partStart <= word.length()) {
      int partEnd = word.indexOf('-', partStart);
      if (partEnd == -1) {
        partEnd = word.length();
      }
      if (partEnd > partStart) {
        String part = word.substring(partStart, partEnd);
        checkWord(part, wordStart + partStart, isNewSentence && partStart == 0, state);
        if (state.terminated) {
          return;
        }
      }
      partStart = partEnd + 1;
    }
  }

  private void reportError(String word, int wordStart, CheckState state) {
    state.errors++;
    state.terminated = fireAndHandleEvent(new BasicSpellCheckEvent(word, wordStart));
  }

  /**
   * The settings and running totals of a single
   * {@link #checkSpelling(WordTokenizer)} call. Each setting is read from
   * the configuration at most once per call, and only if it's needed, rather
   * than for every word. Reading them lazily matters for code, where each
   * comment is checked with its own call and is often only a few words long.
   */
  private static final class CheckState {

    private static final int ANALYZE_CAMEL_CASE = 0;
    private static final int IGNORE_UPPER_CASE = 1;
    private static final int IGNORE_MIXED_CASE = 2;
    private static final int IGNORE_SINGLE_LETTERS = 3;
    private static final int IGNORE_DIGIT_WORDS = 4;
    private static final int IGNORE_INTERNET_ADDRESSES = 5;
    private static final int IGNORE_SENTENCE_CAPITALIZATION = 6;

    private static final String[] KEYS = {
      Configuration.SPELL_ANALYZECAMELCASEWORDS,
      Configuration.SPELL_IGNOREUPPERCASE,
      Configuration.SPELL_IGNOREMIXEDCASE,
      Configuration.SPELL_IGNORESINGLELETTERS,
      Configuration.SPELL_IGNOREDIGITWORDS,
      Configuration.SPELL_IGNOREINTERNETADDRESSES,
      Configuration.SPELL_IGNORESENTENCECAPITALIZATION,
    };

    private final Configuration config;

    /** Bit {@code n} is set once setting {@code n} has been read. */
    private int readSettings;

    /** Bit {@code n} is the value of setting {@code n}, once read. */
    private int settingValues;

    private int errors;
    private boolean terminated;

    private CheckState(Configuration config) {
      this.config = config;
    }

    /**
     * Returns a boolean setting, reading it from the configuration the
     * first time it's asked for.
     *
     * @param setting One of the setting constants in this class.
     * @return The setting's value.
     */
    private boolean get(int setting) {
      int bit = 1 << setting;
      if ((readSettings & bit) == 0) {
        readSettings |= bit;
        if (config.getBoolean(KEYS[setting])) {
          settingValues |= bit;
        }
      }
      return (settingValues & bit) != 0;
    }
  }

}
