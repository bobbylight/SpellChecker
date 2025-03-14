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

import java.util.List;

/**
 * This event is fired off by the SpellChecker and is passed to the
 * registered SpellCheckListeners.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
class BasicSpellCheckEvent implements SpellCheckEvent {

  /**
   * The list holding the suggested Word objects for the misspelled word.
   */
  private List<Word> suggestions;
  /**
   * The misspelled word.
   */
  private String invalidWord;
  /**
   * The action to be done when the event returns.
   */
  private short action = INITIAL;
  /**
   * Contains the word to be replaced if the action is REPLACE or REPLACEALL.
   */
  private String replaceWord;

  private int startPosition;


  /**
   * Constructs the SpellCheckEvent.
   *
   * @param invalidWord The word that is misspelled
   * @param suggestions A list of Word objects that are suggested to replace the currently misspelled word
   * @param tokenizer   The reference to the tokenizer that caused this event to fire.
   */
  BasicSpellCheckEvent(String invalidWord, List<Word> suggestions, WordTokenizer tokenizer) {
    this(invalidWord, suggestions, tokenizer.getCurrentWordPosition());
  }

  /**
   * Constructs the SpellCheckEvent.
   *
   * @param invalidWord   The word that is misspelled
   * @param suggestions   A list of Word objects that are suggested to replace the currently misspelled word
   * @param startPosition The position of the misspelled word.
   */
  BasicSpellCheckEvent(String invalidWord, List<Word> suggestions, int startPosition) {
    this.invalidWord = invalidWord;
    this.suggestions = suggestions;
    this.startPosition = startPosition;
  }

  /**
   * Returns the list of suggested Word objects.
   *
   * @return A list of words phonetically close to the misspelled word
   */
  @Override
  public List<Word> getSuggestions() {
    return suggestions;
  }

  /**
   * Returns the currently misspelled word.
   *
   * @return The text misspelled
   */
  @Override
  public String getInvalidWord() {
    return invalidWord;
  }

  /**
   * Returns the context in which the misspelled word is used.
   *
   * @return The text containing the context
   */
  @Override
  public String getWordContext() {
    //JMH TBD
    return null;
  }

  /**
   * Returns the start position of the misspelled word in the context.
   *
   * @return The position of the word.
   */
  @Override
  public int getWordContextPosition() {
    return startPosition;
  }

  /**
   * Returns the action type the user has to handle.
   *
   * @return The type of action the event is carrying.
   */
  @Override
  public short getAction() {
    return action;
  }

  /**
   * Returns the text to replace.
   *
   * @return the text of the word to replace
   */
  @Override
  public String getReplaceWord() {
    return replaceWord;
  }

  /**
   * Set the action to replace the currently misspelled word with the new word.
   *
   * @param newWord  The word to replace the currently misspelled word
   * @param replaceAll If set to true, the SpellChecker will replace all
   *           further occurrences of the misspelled word without firing a SpellCheckEvent.
   */
  @Override
  public void replaceWord(String newWord, boolean replaceAll) {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    if (replaceAll)
      action = REPLACEALL;
    else
      action = REPLACE;
    replaceWord = newWord;
  }

  /**
   * Set the action it ignore the currently misspelled word.
   *
   * @param ignoreAll If set to true, the SpellChecker will replace all
   *          further occurrences of the misspelled word without firing a SpellCheckEvent.
   */
  @Override
  public void ignoreWord(boolean ignoreAll) {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    if (ignoreAll)
      action = IGNOREALL;
    else
      action = IGNORE;
  }

  /**
   * Set the action to add a new word into the dictionary. This will also replace the
   * currently misspelled word.
   *
   * @param newWord The new word to add to the dictionary.
   */
  @Override
  public void addToDictionary(String newWord) {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    action = ADDTODICT;
    replaceWord = newWord;
  }

  /**
   * Set the action to terminate processing of the spellchecker.
   */
  @Override
  public void cancel() {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    action = CANCEL;
  }
}
