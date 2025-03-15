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

/**
 * This event is fired off by the SpellChecker and is passed to the
 * registered SpellCheckListeners. Modified to only be incorrect words
 * and their locations for RSTA's usage patterns.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
class BasicSpellCheckEvent implements SpellCheckEvent {

  /**
   * The misspelled word.
   */
  private String invalidWord;

  private int startPosition;


  /**
   * Constructs the SpellCheckEvent.
   *
   * @param invalidWord The word that is misspelled
   * @param tokenizer   The reference to the tokenizer that caused this event to fire.
   */
  BasicSpellCheckEvent(String invalidWord, WordTokenizer tokenizer) {
    this(invalidWord, tokenizer.getCurrentWordPosition());
  }

  /**
   * Constructs the SpellCheckEvent.
   *
   * @param invalidWord   The word that is misspelled
   * @param startPosition The position of the misspelled word.
   */
  BasicSpellCheckEvent(String invalidWord, int startPosition) {
    this.invalidWord = invalidWord;
    this.startPosition = startPosition;
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
}
