package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Word}.
 */
class WordTest {

    @Test
    void testConstructor_twoArg() {
        var word = new Word("foo", 5);
        Assertions.assertEquals("foo", word.getWord());
        Assertions.assertEquals(5, word.getCost());
    }

    @Test
    void testCompare_lessThan() {
        var word1 = new Word("foo", 5);
        var word2 = new Word("bar", 10);
        Assertions.assertTrue(Word.COST_COMPARATOR.compare(word1, word2) < 0);
    }

    @Test
    void testCompare_greaterThan() {
        var word1 = new Word("foo", 5);
        var word2 = new Word("bar", 10);
        Assertions.assertTrue(Word.COST_COMPARATOR.compare(word2, word1) > 0);
    }

    @Test
    void testCompare_equal() {
        var word1 = new Word("foo", 5);
        var word2 = new Word("bar", 5);
        Assertions.assertEquals(0, Word.COST_COMPARATOR.compare(word1, word2));
    }

    @Test
    void testEquals_sameWord() {
        var word1 = new Word("foo", 5);
        var word2 = new Word("foo", 6);
        Assertions.assertTrue(word1.equals(word2));
    }

    @Test
    void testEquals_differentWord() {
        var word1 = new Word("foo", 5);
        var word2 = new Word("bar", 5);
        Assertions.assertFalse(word1.equals(word2));
    }

    @Test
    void testEquals_differentType() {
        var word = new Word("foo", 5);
        Assertions.assertFalse(word.equals("foo"));
    }

    @Test
    void testGetWord() {
        var word = new Word("foo", 5);
        Assertions.assertEquals("foo", word.getWord());
    }

    @Test
    void testGetHashCode() {
        var word = new Word("foo", 5);
        Assertions.assertEquals("foo".hashCode(), word.hashCode());
    }

    @Test
    void testToString() {
        var word = new Word("foo", 5);
        Assertions.assertEquals("foo", word.toString());
    }
}
