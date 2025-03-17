/*
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.event.SpellChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SpellingParserNotice}.
 */
class SpellingParserNoticeTest {

    private SpellingParser parser;
    private SpellChecker sc;

    @BeforeEach
    void setUp() throws IOException {
        SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap();
        parser = new SpellingParser(dictionary);
        sc = new SpellChecker(dictionary);
    }

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new SpellingParserNotice(parser, "msg", 0, 4, "word", sc));
    }

    @Test
    void testGetColor() {
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "word", sc);
        assertNotNull(notice.getColor());
    }

    @Test
    void testGetToolTipText() {
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "word", sc);
        assertNotNull(notice.getToolTipText());
    }

    @Test
    void testGetWord() {
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "word", sc);
        assertEquals("word", notice.getWord());
    }

    @Test
    void testToString() {
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "word", sc);
        assertEquals("[SpellingParserNotice: word]", notice.toString());
    }

    @Test
    void testSpellingParserNoticeEquality() {
        SpellingParserNotice notice1 = new SpellingParserNotice(parser, "msg", 0, 4, "word", sc);
        SpellingParserNotice notice2 = new SpellingParserNotice(parser, "msg", 0, 4, "word", sc);
        assertEquals(notice1, notice2);
    }

    @Test
    void testSpellingParserNoticeInequality() {
        SpellingParserNotice notice1 = new SpellingParserNotice(parser, "msg", 0, 4, "word", sc);
        SpellingParserNotice notice2 = new SpellingParserNotice(parser, "msg", 1, 4, "word", sc);
        assertNotEquals(notice1, notice2);
    }

}
