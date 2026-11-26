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
package org.fife.com.swabunga.spell.event;

/**
 * This class tokenizes an input string.
 *
 * @author Jason Height(jheight@chariot.net.au)
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */
public abstract class AbstractWordTokenizer implements WordTokenizer {

    /**
     * The word being analyzed.
     */
    private Word currentWord;
    /**
     * The word finder used to filter out words which are non pertinent to spell checking.
     */
    protected WordFinder finder;

    /**
     * Creates a new AbstractWordTokenizer object.
     *
     * @param text the text to process.
     */
    public AbstractWordTokenizer(String text) {
        this(new DefaultWordFinder(text));
    }

    /**
     * Creates a new AbstractWordTokenizer object.
     *
     * @param wf the custom WordFinder to use in searching for words.
     */
    public AbstractWordTokenizer(WordFinder wf) {
        this.finder = wf;
    }

    /**
     * Returns the end of the current word in the text.
     *
     * @return index in string of the end of the current word.
     * @throws WordNotFoundException current word has not yet been set.
     */
    @Override
    public int getCurrentWordEnd() {
        if (currentWord == null) {
            throw new WordNotFoundException("No Words in current String");
        }
        return currentWord.getEnd();
    }

    /**
     * Returns the index of the start of the current word in the text.
     *
     * @return index in string of the start of the current word.
     * @throws WordNotFoundException current word has not yet been set.
     */
    @Override
    public int getCurrentWordPosition() {
        if (currentWord == null) {
            throw new WordNotFoundException("No Words in current String");
        }
        return currentWord.getStart();
    }

    /**
     * Returns whether there are more words that can be processed in the string.
     *
     * @return Whether there are further words in the text.
     */
    @Override
    public boolean hasMoreWords() {
        return finder.hasNext();
    }

    /**
     * Returns searches for the next word in the text, and returns that word.
     *
     * @return the string representing the current word.
     * @throws WordNotFoundException search string contains no more words.
     */
    @Override
    public String nextWord() {
        currentWord = finder.next();
        return currentWord.getText();
    }

    /**
     * Returns whether the current word is at the start of a sentence.
     *
     * @return Whether the current word starts a sentence.
     * @throws WordNotFoundException current word has not yet been set.
     */
    @Override
    public boolean isNewSentence() {
        return finder.startsSentence();
    }
}
