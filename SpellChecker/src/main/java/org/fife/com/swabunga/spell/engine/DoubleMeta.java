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
package org.fife.com.swabunga.spell.engine;

import org.apache.commons.codec.language.DoubleMetaphone;


/**
 * A phonetic encoding algorithm that takes an English word and computes a
 * phonetic version of it, using Lawrence Philips' Double Metaphone algorithm.
 * This allows for phonetic matches in a spell checker. Only the primary
 * encoding is used.
 * <p>
 * The encoding itself is done by Apache Commons Codec's
 * {@link DoubleMetaphone}, which follows the original C++ implementation.
 * This class adds the list of characters codes are made of, which the spell
 * checker uses to build suggestions.
 * <p>
 * Instances of this class are thread-safe.
 */
public class DoubleMeta implements Transformator {

  /**
   * The maximum length of a code. Commons Codec defaults to 4, but longer
   * codes group words more precisely, which keeps suggestion lookups fast.
   * Codes for longer words are truncated, which only makes such words
   * share a group with words that start with the same sounds.
   */
  private static final int MAX_CODE_LENGTH = 64;

  /**
   * Used in the getSuggestions method.
   * All the letters in the misspelled word are replaced with the characters from
   * this list to try and generate more suggestions, which implies l*n tries,
   * if l is the size of the string, and n is the size of this list.
   * <p>
   * In addition to that, each of these letters is added to the misspelled word.
   * These are all the characters a Double Metaphone code can contain.
   */
  private static final char[] REPLACE_LIST = {
    'A', 'B', 'X', 'S', 'K', 'J', 'T', 'F', 'H', 'L', 'M', 'N', 'P', 'R', '0'
  };

  private final DoubleMetaphone encoder;

  /**
   * Constructor.
   */
  public DoubleMeta() {
    encoder = new DoubleMetaphone();
    encoder.setMaxCodeLen(MAX_CODE_LENGTH);
  }

  /**
   * Take the given word, and return the best phonetic hash for it.
   * Vowels are minimized as much as possible, and consonants
   * that have similar sounds are converted to the same consonant
   * for example, 'v' and 'f' are both converted to 'f'
   *
   * @param word the text to transform
   * @return the result of the phonetic transformation, or an empty string
   *         if {@code word} is {@code null} or blank.
   */
  @Override
  public final String transform(String word) {
    String code = encoder.doubleMetaphone(word);
    return code == null ? "" : code;
  }

  @Override
  public char[] getReplaceList() {
    return REPLACE_LIST;
  }
}
