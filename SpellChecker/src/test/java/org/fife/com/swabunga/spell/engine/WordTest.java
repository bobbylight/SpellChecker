package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Word}.
 */
class WordTest {

    @Test
    void testConstructor_zeroArg() {
        Word word = new Word();
        Assertions.assertEquals("", word.getWord());
        Assertions.assertEquals(0, word.getCost());
    }

    @Test
    void testConstructor_twoArg() {
        Word word = new Word("foo", 5);
        Assertions.assertEquals("foo", word.getWord());
        Assertions.assertEquals(5, word.getCost());
    }

    @Test
    void testCompare_lessThan() {
        Word word1 = new Word("foo", 5);
        Word word2 = new Word("bar", 10);
        Assertions.assertEquals(-1, word1.compare(word1, word2));
    }

    @Test
    void testCompare_greaterThan() {
        Word word1 = new Word("foo", 5);
        Word word2 = new Word("bar", 10);
        Assertions.assertEquals(1, word1.compare(word2, word1));
    }

    @Test
    void testCompare_equal() {
        Word word1 = new Word("foo", 5);
        Word word2 = new Word("bar", 5);
        Assertions.assertEquals(0, word1.compare(word1, word2));
    }

    @Test
    void testEquals_sameWord() {
        Word word1 = new Word("foo", 5);
        Word word2 = new Word("foo", 6);
        Assertions.assertTrue(word1.equals(word2));
    }

    @Test
    void testEquals_differentWord() {
        Word word1 = new Word("foo", 5);
        Word word2 = new Word("bar", 5);
        Assertions.assertFalse(word1.equals(word2));
    }

    @Test
    void testEquals_differentType() {
        Word word = new Word("foo", 5);
        Assertions.assertFalse(word.equals("foo"));
    }

    @Test
    void testGetSetWord() {
        Word word = new Word("foo", 5);
        Assertions.assertEquals("foo", word.getWord());
        word.setWord("bar");
        Assertions.assertEquals("bar", word.getWord());
    }

    @Test
    void testGetHashCode() {
        Word word = new Word("foo", 5);
        Assertions.assertEquals("foo".hashCode(), word.hashCode());
    }

    @Test
    void testToString() {
        Word word = new Word("foo", 5);
        Assertions.assertEquals("foo", word.toString());
    }
}
