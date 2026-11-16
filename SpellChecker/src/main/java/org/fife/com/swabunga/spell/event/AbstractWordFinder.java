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

import java.text.BreakIterator;

/**
 * Defines common methods and behaviour for the various word finding
 * subclasses.
 *
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */
public abstract class AbstractWordFinder implements WordFinder {

  /**
   * The word being analyzed.
   */
  protected Word currentWord;
  /**
   * The word following the current one.
   */
  protected Word nextWord;
  /**
   * Indicate if the current word starts a new sentence.
   */
  protected boolean startsSentence;
  /**
   * Holds the text to analyze.
   */
  protected String text;

  /**
   * An iterator to work through the sentence.
   */
  protected BreakIterator sentenceIterator;

  /**
   * Creates a new AbstractWordFinder object.
   *
   * @param inText the String to iterate through.
   */
  public AbstractWordFinder(String inText) {
    text = inText;
    setup();
  }

  /**
   * Creates a new default AbstractWordFinder object.
   */
  public AbstractWordFinder() {
    text = "";
    setup();
  }

  /**
   * This method scans the text from the end of the last word,  and returns
   * a new Word object corresponding to the next word.
   *
   * @return the following word.
   */
  @Override
  public abstract Word next();

  /**
   * Return the text being searched.
   *
   * @return the text being searched.
   */
  @Override
  public String getText() {
    return text;
  }

  /**
   * Defines the text to search.
   *
   * @param newText The text to be analyzed
   */
  @Override
  public void setText(String newText) {
    text = newText;
    setup();
  }

  /**
   * Returns the current word in the iteration .
   *
   * @return the current word.
   * @throws WordNotFoundException current word has not yet been set.
   */
  @Override
  public Word current() {
    if (currentWord == null) {
      throw new WordNotFoundException("No Words in current String");
    }
    return currentWord;
  }

  /**
   * Indicates if there is some more word to analyze.
   *
   * @return true if there are further words in the string.
   */
  @Override
  public boolean hasNext() {
    return nextWord != null;
  }

  /**
   * @return true if the current word starts a new sentence.
   * @throws WordNotFoundException current word has not yet been set.
   */
  @Override
  public boolean startsSentence() {
    if (currentWord == null) {
      throw new WordNotFoundException("No Words in current String");
    }
    return startsSentence;
  }

  /**
   * Return the text being searched.
   *
   * @return the text being searched.
   */
  @Override
  public String toString() {
    return text;
  }

  /**
   * Adjusts the sentence iterator and the startSentence flag according to the
   * currentWord.
   *
   * @param wd the wd parameter is not presently used.
   */
  protected void setSentenceIterator(Word wd) {
    int current = sentenceIterator.current();

    if (current == currentWord.getStart())
      startsSentence = true;
    else {
      startsSentence = false;

      if (currentWord.getEnd() > current) {
        sentenceIterator.next();
      }
    }
  }

  /**
   * Indicates if the character at the specified position is acceptable as
   * part of a word. To be acceptable, the character needs to be a letter
   * or a digit, or an apostrophe preceded and followed by a letter or digit.
   *
   * @param posn The character position to analyze.
   * @return Whether the character is part of a word.
   */
  protected boolean isWordChar(int posn) {
    char curr = text.charAt(posn);

    if ((posn == 0) || (posn == text.length() - 1)) {
      return Character.isLetterOrDigit(curr);
    }

    // An apostrophe between letters or digits is part of a word, as in "can't"
    if (curr == '\'') {
      return Character.isLetterOrDigit(text.charAt(posn - 1)) &&
          Character.isLetterOrDigit(text.charAt(posn + 1));
    }

    return Character.isLetterOrDigit(curr);
  }

  /**
   * Initializes the sentenceIterator.
   */
  protected void init() {
    sentenceIterator = BreakIterator.getSentenceInstance();
    sentenceIterator.setText(text);
  }

  /**
   * Defines the starting positions for text analysis.
   */
  private void setup() {
    currentWord = new Word("", 0);
    nextWord = new Word("", 0);
    startsSentence = true;

    init();

    try {
      next();
    } catch (WordNotFoundException e) {
      currentWord = null;
      nextWord = null;
    }
  }

}
