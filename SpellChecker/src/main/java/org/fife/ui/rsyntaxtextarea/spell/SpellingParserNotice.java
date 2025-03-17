/*
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.event.SpellChecker;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;

import java.awt.*;

/**
 * A parser notice for spelling errors.
 */
class SpellingParserNotice extends DefaultParserNotice {

    private String word;
    private SpellChecker sc;

    SpellingParserNotice(SpellingParser parser, String msg,
                         int line, int offs, String word,
                         SpellChecker sc) {
        super(parser, msg, line, offs, word.length());
        setLevel(Level.INFO);
        this.word = word;
        this.sc = sc;
    }

    @Override
    public Color getColor() {
        return ((SpellingParser)getParser()).getSquiggleUnderlineColor();
    }

    @Override
    public String getToolTipText() {
        return new SpellingErrorTooltipHtmlGenerator().get(sc, this);
    }

    /**
     * Returns the incorrectly spelled word.
     *
     * @return The word.
     */
    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "[SpellingParserNotice: " + word + "]";
    }

}
