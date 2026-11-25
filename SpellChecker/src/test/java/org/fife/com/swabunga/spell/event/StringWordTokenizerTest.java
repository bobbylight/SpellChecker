package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StringWordTokenizer}.
 */
class StringWordTokenizerTest {

    private StringWordTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new StringWordTokenizer("This is a test.");
    }

    @Test
    void testConstructor_withString() {
        assertEquals("This", tokenizer.nextWord());
    }

    @Test
    void testConstructor_withWordFinder() {
        WordFinder finder = new DefaultWordFinder("Another test.");
        tokenizer = new StringWordTokenizer(finder);
        assertEquals("Another", tokenizer.nextWord());
    }

    @Test
    void testConstructor_withStringAndWordFinder() {
        WordFinder finder = new DefaultWordFinder();
        tokenizer = new StringWordTokenizer("Yet another test.", finder);
        assertEquals("Yet", tokenizer.nextWord());
    }

    @Test
    void testGetCurrentWordEnd() {
        tokenizer.nextWord();
        assertEquals("This".length(), tokenizer.getCurrentWordEnd());
    }

    @Test
    void testGetCurrentWordEnd_errorIfNoWord() {
        assertThrows(WordNotFoundException.class, () -> tokenizer.getCurrentWordEnd());
    }

    @Test
    void testGetCurrentWordPosition() {
        tokenizer.nextWord();
        assertEquals(0, tokenizer.getCurrentWordPosition());
    }

    @Test
    void testGetCurrentWordPosition_errorIfNoWord() {
        assertThrows(WordNotFoundException.class, () -> tokenizer.getCurrentWordPosition());
    }
}
