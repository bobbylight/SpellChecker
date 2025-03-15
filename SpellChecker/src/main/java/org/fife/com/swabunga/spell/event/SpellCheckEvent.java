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
 * registered SpellCheckListeners
 * <p/>
 * As far as I know, we will only require one implementation of the SpellCheckEvent
 * (BasicSpellCheckEvent) but I have defined this interface just in case. The
 * BasicSpellCheckEvent implementation is currently package private.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public interface SpellCheckEvent {

  /**
   * Returns the currently misspelled word.
   *
   * @return The text misspelled
   */
  String getInvalidWord();

  /**
   * Returns the context in which the misspelled word is used.
   *
   * @return The text containing the context
   */
  String getWordContext();

  /**
   * Returns the start position of the misspelled word in the context.
   *
   * @return The position of the word
   */
  int getWordContextPosition();
}
