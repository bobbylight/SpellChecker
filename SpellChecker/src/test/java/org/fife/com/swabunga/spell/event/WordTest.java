package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Word}.
 */
class WordTest {

    @Test
    void testConstructor() {
        var word = new Word("test", 0);
        assertEquals("test", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(4, word.getEnd());
    }

    @Test
    void testCopyConstructor() {
        var original = new Word("test", 0);
        var copy = new Word(original);
        assertEquals("test", copy.getText());
        assertEquals(0, copy.getStart());
        assertEquals(4, copy.getEnd());
    }

    @Test
    void testSetText() {
        var word = new Word("test", 0);
        word.setText("example");
        assertEquals("example", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(7, word.getEnd());
    }

    @Test
    void testSetStart() {
        var word = new Word("test", 0);
        word.setStart(5);
        assertEquals(5, word.getStart());
        assertEquals(9, word.getEnd());
    }

    @Test
    void testLength() {
        var word = new Word("test", 0);
        assertEquals(4, word.length());
    }

    @Test
    void testToString() {
        var word = new Word("test", 0);
        assertEquals("test", word.toString());
    }
}
