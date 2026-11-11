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

    /**
     * The tooltip, computed the first time it's asked for. Swing asks again
     * on every mouse move while a tooltip is showing, and each time means
     * computing spelling suggestions. A notice only lives until the next
     * parse, and adding or ignoring a word forces a reparse, so this never
     * outlives the dictionaries it was computed from.
     */
    private String toolTipText;

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
        if (toolTipText == null) {
            toolTipText = new SpellingErrorTooltipHtmlGenerator().get(sc, this);
        }
        return toolTipText;
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
