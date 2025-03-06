package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Word}.
 */
class WordTest {

    @Test
    void testConstructor() {
        Word word = new Word("test", 0);
        assertEquals("test", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(4, word.getEnd());
    }

    @Test
    void testCopyConstructor() {
        Word original = new Word("test", 0);
        Word copy = new Word(original);
        assertEquals("test", copy.getText());
        assertEquals(0, copy.getStart());
        assertEquals(4, copy.getEnd());
    }

    @Test
    void testSetText() {
        Word word = new Word("test", 0);
        word.setText("example");
        assertEquals("example", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(7, word.getEnd());
    }

    @Test
    void testSetStart() {
        Word word = new Word("test", 0);
        word.setStart(5);
        assertEquals(5, word.getStart());
        assertEquals(9, word.getEnd());
    }

    @Test
    void testLength() {
        Word word = new Word("test", 0);
        assertEquals(4, word.length());
    }

    @Test
    void testToString() {
        Word word = new Word("test", 0);
        assertEquals("test", word.toString());
    }
}
