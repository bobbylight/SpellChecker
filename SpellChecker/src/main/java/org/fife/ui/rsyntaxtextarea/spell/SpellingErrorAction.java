/*
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

/**
 * The actions someone can take with a misspelled word.
 */
public enum SpellingErrorAction {

    /**
     * Add the word to the user's dictionary.
     */
    ADD,

    /**
     * Replace the misspelled word with a suggested word.
     */
    REPLACE,

    /**
     * Ignore the misspelled word.
     */
    IGNORE
}
