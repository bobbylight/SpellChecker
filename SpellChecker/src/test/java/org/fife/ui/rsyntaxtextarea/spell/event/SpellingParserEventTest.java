package org.fife.ui.rsyntaxtextarea.spell.event;

import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code SpellingParserEvent}.
 */
public class SpellingParserEventTest {

    private SpellingParser parser;
    private RSyntaxTextArea textArea;

    @BeforeEach
    void setUp() throws IOException {
        parser = new SpellingParser(new SpellDictionaryHashMap());
        textArea = new RSyntaxTextArea();
    }

    @Test
    void testConstructor_wordAdded() {
        SpellingParserEvent e = new SpellingParserEvent(parser, textArea, SpellingParserEvent.WORD_ADDED, "test");
        assertNotNull(e);
    }

    @Test
    void testConstructor_wordIgnored() {
        SpellingParserEvent e = new SpellingParserEvent(parser, textArea, SpellingParserEvent.WORD_IGNORED, "test");
        assertNotNull(e);
    }

    @Test
    void testConstructor_error_invalidEventType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SpellingParserEvent(parser, textArea, -1, "test");
        });
    }

    @Test
    void testGetParser() {
        SpellingParserEvent e = new SpellingParserEvent(parser, textArea, SpellingParserEvent.WORD_ADDED, "test");
        assertEquals(parser, e.getParser());
    }

    @Test
    void testGetTextArea() {
        SpellingParserEvent e = new SpellingParserEvent(parser, textArea, SpellingParserEvent.WORD_ADDED, "test");
        assertEquals(textArea, e.getTextArea());
    }

    @Test
    void testGetType() {
        SpellingParserEvent e = new SpellingParserEvent(parser, textArea, SpellingParserEvent.WORD_ADDED, "test");
        assertEquals(SpellingParserEvent.WORD_ADDED, e.getType());
    }

    @Test
    void testGetWord() {
        SpellingParserEvent e = new SpellingParserEvent(parser, textArea, SpellingParserEvent.WORD_ADDED, "test");
        assertEquals("test", e.getWord());
    }
}
