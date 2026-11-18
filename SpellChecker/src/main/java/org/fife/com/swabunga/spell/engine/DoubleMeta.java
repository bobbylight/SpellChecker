/*
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
 */
package org.fife.com.swabunga.spell.engine;

import java.util.List;

import org.apache.commons.codec.language.DoubleMetaphone;


/**
 * Computes the phonetic codes of words, using Lawrence Philips' Double
 * Metaphone algorithm via Apache Commons Codec's {@link DoubleMetaphone}.
 * <p>
 * Double Metaphone gives a word a primary code and, when a spelling has two
 * plausible pronunciations, a different alternate code. For example,
 * "special" is primarily {@code SPSL}, from the Italian-style reading of
 * "CIA", and alternately {@code SPXL}, the English "shul" sound. Words that
 * sound alike share at least one code.
 * <p>
 * Instances of this class are thread-safe.
 */
final class DoubleMeta {

  /**
   * Every character a code can contain. Suggestions try replacing and
   * inserting each of these in a misspelled word.
   */
  static final char[] CODE_CHARS = {
    'A', 'B', 'X', 'S', 'K', 'J', 'T', 'F', 'H', 'L', 'M', 'N', 'P', 'R', '0'
  };

  /**
   * The maximum length of a code. Commons Codec defaults to 4, but longer
   * codes group words more precisely, which keeps suggestion lookups fast.
   * Codes for longer words are truncated, which only makes such words
   * share a group with words that start with the same sounds.
   */
  private static final int MAX_CODE_LENGTH = 64;

  private final DoubleMetaphone encoder;

  DoubleMeta() {
    encoder = new DoubleMetaphone();
    encoder.setMaxCodeLen(MAX_CODE_LENGTH);
  }

  /**
   * Returns a word's primary code.
   *
   * @param word The word.
   * @return The primary code, or an empty string if {@code word} is
   *         {@code null} or blank.
   */
  String getPrimaryCode(String word) {
    String code = encoder.doubleMetaphone(word);
    return code == null ? "" : code;
  }

  /**
   * Returns a word's codes: its primary code, followed by its alternate
   * code if that's different.
   *
   * @param word The word.
   * @return The codes. This is {@code [""]} if {@code word} is {@code null}
   *         or blank.
   */
  List<String> getCodes(String word) {
    String primary = getPrimaryCode(word);
    if (primary.isEmpty()) {
      return List.of(primary);
    }
    String alternate = encoder.doubleMetaphone(word, true);
    return primary.equals(alternate) ? List.of(primary) : List.of(primary, alternate);
  }
}
