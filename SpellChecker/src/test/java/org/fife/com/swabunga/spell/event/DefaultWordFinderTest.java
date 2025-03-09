package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DefaultWordFinder}.
 */
class DefaultWordFinderTest {

    private DefaultWordFinder wordFinder;

    @BeforeEach
    void setUp() {
        wordFinder = new DefaultWordFinder("This is a test.");
    }

    @Test
    void testCurrent() {
        Word next = wordFinder.next();
        assertEquals(next, wordFinder.current());
    }

    @Test
    void testGetText() {
        assertEquals("This is a test.", wordFinder.getText());
    }

    @Test
    void testIsWordChar_asciiLettersAndNumbers() {
        for (int i = 'a'; i <= 'z'; i++) {
            wordFinder = new DefaultWordFinder(String.valueOf((char)i));
            assertTrue(wordFinder.isWordChar(0));
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            wordFinder = new DefaultWordFinder(String.valueOf((char)i));
            assertTrue(wordFinder.isWordChar(0));
        }
        for (int i = '0'; i <= '9'; i++) {
            wordFinder = new DefaultWordFinder(String.valueOf((char)i));
            assertTrue(wordFinder.isWordChar(0));
        }
    }

    @Test
    void testIsWordChar_apostrophe() {
        // By itself, it is not a word char
        wordFinder = new DefaultWordFinder("'");
        assertFalse(wordFinder.isWordChar(0));

        // In a contraction (in between letters), it is
        wordFinder = new DefaultWordFinder("can't");
        for (int i = 0; i < wordFinder.getText().length(); i++) {
            assertTrue(wordFinder.isWordChar(i));
        }

        // At the end of input, it isn't
        wordFinder = new DefaultWordFinder("can'");
        assertFalse(wordFinder.isWordChar(wordFinder.getText().length() - 1));

        // If in between a letter and a non-word char, it isn't
        wordFinder = new DefaultWordFinder("can'#");
        assertTrue(wordFinder.isWordChar(0));
        assertTrue(wordFinder.isWordChar(1));
        assertTrue(wordFinder.isWordChar(2));
        assertFalse(wordFinder.isWordChar(3));
        assertFalse(wordFinder.isWordChar(4));
    }

    @Test
    void testIsWordChar_nonWordChar() {
        wordFinder = new DefaultWordFinder("#");
        assertFalse(wordFinder.isWordChar(0));
    }

    @Test
    void testNextWord_oneArgConstructor() {
        Word word = wordFinder.next();
        assertEquals("This", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(4, word.getEnd());

        word = wordFinder.next();
        assertEquals("is", word.getText());
        assertEquals(5, word.getStart());
        assertEquals(7, word.getEnd());

        word = wordFinder.next();
        assertEquals("a", word.getText());
        assertEquals(8, word.getStart());
        assertEquals(9, word.getEnd());

        word = wordFinder.next();
        assertEquals("test", word.getText());
        assertEquals(10, word.getStart());
        assertEquals(14, word.getEnd());
    }

    @Test
    void testNextWord_zeroArgConstructor() {
        wordFinder = new DefaultWordFinder();
        assertThrows(WordNotFoundException.class, wordFinder::next);
    }

    @Test
    void testNextWord_noMoreWords() {
        wordFinder.next(); // "This"
        wordFinder.next(); // "is"
        wordFinder.next(); // "a"
        wordFinder.next(); // "test"
        assertThrows(WordNotFoundException.class, () -> wordFinder.next());
    }

    @Test
    void testNextWord_emptyString() {
        DefaultWordFinder emptyFinder = new DefaultWordFinder("");
        assertThrows(WordNotFoundException.class, emptyFinder::next);
    }

    @Test
    void testNextWord_url_carriageReturn() {
        DefaultWordFinder finder = new DefaultWordFinder("https://google.com\r\nfoo");
        Word word = finder.next();
        assertEquals("https://google.com", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(18, word.getEnd());
    }

    @Test
    void testNextWord_url_endOfInput() {
        DefaultWordFinder finder = new DefaultWordFinder("https://google.com");
        Word word = finder.next();
        assertEquals("https://google.com", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(18, word.getEnd());
    }

    @Test
    void testNextWord_url_lineFeed() {
        DefaultWordFinder finder = new DefaultWordFinder("https://google.com\nfoo");
        Word word = finder.next();
        assertEquals("https://google.com", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(18, word.getEnd());
    }

    @Test
    void testNextWord_url_whitespace() {
        DefaultWordFinder finder = new DefaultWordFinder("https://google.com foo");
        Word word = finder.next();
        assertEquals("https://google.com", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(18, word.getEnd());
    }

    @Test
    void testNextWord_url_commaAndWhitespace() {
        DefaultWordFinder finder = new DefaultWordFinder("https://google.com, foo");
        Word word = finder.next();
        assertEquals("https://google.com", word.getText());
        assertEquals(0, word.getStart());
        assertEquals(18, word.getEnd());
    }
}
