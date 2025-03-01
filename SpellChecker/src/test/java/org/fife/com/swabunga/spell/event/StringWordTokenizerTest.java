package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

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
        assertNotNull(tokenizer);
        assertEquals("This is a test.", tokenizer.getContext());
    }

    @Test
    void testConstructor_withWordFinder() {
        WordFinder finder = new DefaultWordFinder("Another test.");
        tokenizer = new StringWordTokenizer(finder);
        assertNotNull(tokenizer);
        assertEquals("Another test.", tokenizer.getContext());
    }

    @Test
    void testConstructor_withStringAndWordFinder() {
        WordFinder finder = new DefaultWordFinder();
        tokenizer = new StringWordTokenizer("Yet another test.", finder);
        assertNotNull(tokenizer);
        assertEquals("Yet another test.", tokenizer.getContext());
    }

    @Test
    void testGetCurrentWordCount() {
        assertEquals(0, tokenizer.getCurrentWordCount());
        tokenizer.nextWord();
        assertEquals(1, tokenizer.getCurrentWordCount());
        tokenizer.nextWord();
        assertEquals(2, tokenizer.getCurrentWordCount());
    }

    @Test
    void testGetCurrentWordEnd() {
        tokenizer.nextWord();
        assertEquals("This".length(), tokenizer.getCurrentWordEnd());
    }

    @Test
    void testGetFinalText() {
        assertEquals("This is a test.", tokenizer.getFinalText());
    }

    @Test
    void testGetWordEnd() {
        WordFinder finder = new DefaultWordFinder();
        tokenizer = new StringWordTokenizer("Yet another test.", finder);
        assertNotNull(tokenizer);
        assertEquals("Yet another test.", tokenizer.getContext());
    }

    @Test
    void testReplaceWord_beforeAnyWords() {
        tokenizer.replaceWord("That");
        assertEquals("ThatThis is a test.", tokenizer.getContext());
    }

    @Test
    void testReplaceWord_onFirstWord() {
        tokenizer.nextWord(); // We're initially before any word
        tokenizer.replaceWord("That");
        assertEquals("That is a test.", tokenizer.getContext());
    }

    @Test
    void testReplaceWord_noCurrentWord() {
        assertThrows(WordNotFoundException.class, () -> {
            WordFinder wordFinder = new DefaultWordFinder();
            wordFinder.next();
            tokenizer = new StringWordTokenizer(wordFinder);
            tokenizer.replaceWord("That");
        });
    }
}
