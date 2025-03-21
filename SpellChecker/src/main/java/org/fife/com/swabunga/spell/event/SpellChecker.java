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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fife.com.swabunga.spell.engine.Configuration;
import org.fife.com.swabunga.spell.engine.SpellDictionary;
import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.engine.Word;
import org.fife.com.swabunga.util.ListUtil;


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
  /**
   * Flag indicating that the Spell Check completed without any errors present.
   */
  public static final int SPELLCHECK_OK = -1;
  /**
   * Flag indicating that the Spell Check completed due to user cancellation.
   */
  public static final int SPELLCHECK_CANCEL = -2;

  private List<SpellCheckListener> eventListeners = new ArrayList<>();
  private List<SpellDictionary> dictionaries = new ArrayList<>();
  private SpellDictionary userDictionary;

  private Configuration config = Configuration.getConfiguration();

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
   * Constructs the SpellChecker. The default threshold is used
   *
   * @param dictionary The dictionary used for looking up words.
   */
  public SpellChecker(SpellDictionary dictionary) {
    try {
      userDictionary = new SpellDictionaryHashMap();
    } catch (IOException e) {
      throw new RuntimeException("this exception should never happen because we are using null phonetic file", e);
    }
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
    if (dictionary == null) {
      throw new IllegalArgumentException("dictionary must be non-null");
    }
    this.dictionaries.add(dictionary);
  }

  /**
   * Registers the user dictionary to which words are added.
   *
   * @param dictionary the dictionary to use when the user specify a new word
   *        to add. This cannot be {@code null}.
   */
  public void setUserDictionary(SpellDictionary dictionary) {
    if (dictionary == null) {
      throw new IllegalArgumentException("dictionary must be non-null");
    }
    userDictionary = dictionary;
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
    for (int i = word.length() - 1; i >= 0; i--) {
      if (Character.isDigit(word.charAt(i))) {
        return true;
      }
    }
    return false;
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
   * </ul>
   * <p>
   * One limitation is that this method cannot currently recognize email
   * addresses. Since the 'word' that is passed in, may in fact contain
   * the rest of the document to be checked, it is not (yet!) a good
   * idea to scan for the @ character.
   *
   * @param word The word to analyze for an Internet address.
   * @return true if this word looks like an Internet address.
   * @see #isINETWord(String)
   */
  public static boolean beginsAsINETWord(String word) {
    // robert: In standard Jazzy distributions, this is "isINETWord(String)".
    // robert: Since "word" may be the entire rest of the document (line), we'll try
    // to micro-optimize a little here and just get the smallest lower-case String
    // we need.
    //String lowerCaseWord = word.toLowerCase();
    int last = Math.min(8, word.length());
    String lowerCaseWord = word.substring(0, last);
    return lowerCaseWord.startsWith("http://") ||
        lowerCaseWord.startsWith("www.") ||
        lowerCaseWord.startsWith("ftp://") ||
        lowerCaseWord.startsWith("https://") ||
        lowerCaseWord.startsWith("ftps://");
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
   * <code>beginsAsINETWord(String)</code>).
   *
   * @param word The word to analyze for an Internet address.
   * @return true if this word looks like an Internet address.
   * @see #beginsAsINETWord(String)
   */
  public static boolean isINETWord(String word) {
    return beginsAsINETWord(word) || word.indexOf('@') > 0;
  }


  /**
   * Verifies if the word that is being spell checked contains all
   * upper-cased characters.
   *
   * @param word The word to analyze for upper-cases characters
   * @return true if this word contains all upper case characters
   */
  private static boolean isUpperCaseWord(CharSequence word) {
    for (int i = word.length() - 1; i >= 0; i--) {
      if (Character.isLowerCase(word.charAt(i))) {
        return false;
      }
    }
    return true;
  }


  /**
   * Verifies if the word that is being spell checked contains lower and
   * upper-cased characters. Note that a phrase beginning with an upper-cased
   * character is not considered a mixed case word.
   *
   * @param word       The word to analyze for mixed cases characters
   * @param startsSentence True if this word is at the start of a sentence
   * @return true if this word contains mixed case characters
   */
  private static boolean isMixedCaseWord(CharSequence word, boolean startsSentence) {
    int strLen = word.length();
    boolean isUpper = Character.isUpperCase(word.charAt(0));
    //Ignore the first character if this word starts the sentence and the first
    //character was upper-cased, since this is normal behaviour
    if ((startsSentence) && isUpper && (strLen > 1))
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
   * <li>The default user dictionary or the one set through
   * {@link SpellChecker#setUserDictionary}</li>
   * <li>The dictionary specified at construction time, if any.</li>
   * <li>Any dictionary in the order they were added through
   * {@link SpellChecker#addDictionary}</li>
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
   *
   * @param word    The word for which we want to gather suggestions
   * @param threshold the cost value above which any suggestions are
   *          thrown away
   * @return the list of words suggested
   */
  public List<Word> getSuggestions(String word, int threshold) {
    //long start = System.currentTimeMillis();
    if (this.threshold != threshold && cache != null) {
      this.threshold = threshold;
      cache.clear();
    }

    List<Word> suggestions = null;

    if (cache != null) {
        suggestions = cache.get(word);
    }

    if (suggestions == null) {
      suggestions = new ArrayList<>();

      for (SpellDictionary dictionary : dictionaries) {
        if (dictionary != userDictionary)
          ListUtil.addAllNoDuplicates(suggestions, dictionary.getSuggestions(word, threshold));
      }

      if (cache != null && cache.size() < cacheSize)
        cache.put(word, suggestions);
    }

    ListUtil.addAllNoDuplicates(suggestions, userDictionary.getSuggestions(word, threshold));
    if (suggestions instanceof ArrayList) {
      ((ArrayList<Word>)suggestions).trimToSize();
    }

    //long time = System.currentTimeMillis() - start;
    //float secs = time/1000f;
    //System.out.println("[DEBUG]: Suggestions for '" + word + "' took " + secs + " seconds");
    return suggestions;
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
   * @return Either SPELLCHECK_OK, SPELLCHECK_CANCEL or the number of errors found. The number of errors are those that
   *         are found BEFORE any corrections are made.
   */
  public final int checkSpelling(WordTokenizer tokenizer) {
    int errors = 0;
    boolean terminated = false;
    //Keep track of the previous word
    //  String previousWord = null;
    while (tokenizer.hasMoreWords() && !terminated) {
      String word = tokenizer.nextWord();
      //Check the spelling of the word
      if (!isCorrect(word)) {

        boolean isNewSentence = tokenizer.isNewSentence();
        boolean isMixedCaseWord = isMixedCaseWord(word, isNewSentence);

        // robert: If this is a mixed-case word, check spelling of each part
        if (config.getBoolean(Configuration.SPELL_ANALYZECAMELCASEWORDS) && isMixedCaseWord) {

          List<String> parts = splitMixedCaseWord(word);

          int offs = 0;
          for (String part : parts) {

            // Ignore mixed-case word parts, if necessary
            if (!config.getBoolean(Configuration.SPELL_IGNOREUPPERCASE) || !isUpperCaseWord(part)) {
              String partLower = part.toLowerCase();
              if (!isCorrect(partLower) && !isIgnored(partLower)) {
                errors++;
                int wordOffs = tokenizer.getCurrentWordPosition() + offs;
                SpellCheckEvent event = new BasicSpellCheckEvent(part, wordOffs);
                terminated = fireAndHandleEvent(event);
                if (terminated) {
                  break;
                }
              }
            }

            offs += part.length();
          }
        }
        else if ((config.getBoolean(Configuration.SPELL_IGNOREMIXEDCASE) && isMixedCaseWord) ||
            (config.getBoolean(Configuration.SPELL_IGNOREUPPERCASE) && isUpperCaseWord(word)) ||
            (config.getBoolean(Configuration.SPELL_IGNORESINGLELETTERS) && word.length() == 1) ||
            (config.getBoolean(Configuration.SPELL_IGNOREDIGITWORDS) && isDigitWord(word)) ||
            (config.getBoolean(Configuration.SPELL_IGNOREINTERNETADDRESSES) && isINETWord(word))) {
          //Null event. Since we are ignoring this word due
          //to one of the above cases.
        }
        else {
          //We can't ignore this misspelled word
          //For this invalid word are we ignoring the misspelling?
          if (!isIgnored(word)) {
            errors++;
            SpellCheckEvent event = new BasicSpellCheckEvent(word, tokenizer);
            terminated = fireAndHandleEvent(event);
          }
        }
      }
      else {
        //This is a correctly spelled word. However, perform some extra checks
        /*
         *  JMH TBD      //Check for multiple words
         *  if (!ignoreMultipleWords &&) {
         *  }
         */
        //Check for capitalization
        if (isSupposedToBeCapitalized(word, tokenizer)) {
          errors++;
          SpellCheckEvent event = new BasicSpellCheckEvent(word, tokenizer);
          terminated = fireAndHandleEvent(event);
        }
      }
    }

    if (terminated)
      return SPELLCHECK_CANCEL;
    else if (errors == 0)
      return SPELLCHECK_OK;
    else
      return errors;
  }

  private boolean isSupposedToBeCapitalized(String word, WordTokenizer wordTokenizer) {
    boolean configCapitalize = !config.getBoolean(Configuration.SPELL_IGNORESENTENCECAPITALIZATION);
    return configCapitalize && wordTokenizer.isNewSentence() && Character.isLowerCase(word.charAt(0));
  }

}
