package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code XMLWordFinder}.
 */
class XMLWordFinderTest {

    @Test
    void testCurrent() {
        String xml = "<root><element>word</element></root>";
        XMLWordFinder finder = new XMLWordFinder(xml);
        Word next = finder.next();
        assertEquals(next, finder.current());
    }

    @Test
    void testFindWords_validXML() {
        String xml = "<root><element>word</element></root>";
        XMLWordFinder finder = new XMLWordFinder(xml);
        Word next = finder.next();
        assertEquals("word", next.getText());
        assertEquals(15, next.getStart());
        assertEquals(19, next.getEnd());
        assertFalse(finder.hasNext());
    }

    @Test
    void testFindWords_emptyXML() {
        String xml = "<root></root>";
        XMLWordFinder finder = new XMLWordFinder(xml);
        assertFalse(finder.hasNext());
    }

    @Test
    void testFindWords_invalidXML() {
        String xml = "<root><element>word</element>";
        XMLWordFinder finder = new XMLWordFinder(xml);
        Word next = finder.next();
        assertEquals("word", next.getText());
        assertEquals(15, next.getStart());
        assertEquals(19, next.getEnd());
        assertFalse(finder.hasNext());
    }

    @Test
    void testFindWords_multipleWords() {
        String xml = "<root><element>word1</element><element>word2</element></root>";
        XMLWordFinder finder = new XMLWordFinder(xml);
        Word next = finder.next();
        assertEquals("word1", next.getText());
        assertEquals(15, next.getStart());
        assertEquals(20, next.getEnd());
        next = finder.next();
        assertEquals("word2", next.getText());
        assertEquals(39, next.getStart());
        assertEquals(44, next.getEnd());
        assertFalse(finder.hasNext());
    }

    @Test
    void testFindWords_noWordsThrows() {
        String xml = "<root><element></element></root>";
        XMLWordFinder finder = new XMLWordFinder(xml);
        assertThrows(WordNotFoundException.class, finder::next);
    }

    @Test
    void testFindWords_wordAtEnd() {
        String xml = "<root>word";
        XMLWordFinder finder = new XMLWordFinder(xml);
        Word next = finder.next();
        assertEquals("word", next.getText());
        assertEquals(6, next.getStart());
        assertEquals(10, next.getEnd());
    }

    @Test
    void testGetText() {
        String xml = "<root><element>This is a test.</element></root>";
        XMLWordFinder finder = new XMLWordFinder(xml);
        assertEquals(xml, finder.getText());
    }

    @Test
    void testIsWordChar_asciiLettersAndNumbers() {
        for (int i = 'a'; i <= 'z'; i++) {
            XMLWordFinder finder = new XMLWordFinder(String.valueOf((char)i));
            assertTrue(finder.isWordChar(0));
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            XMLWordFinder finder = new XMLWordFinder(String.valueOf((char)i));
            assertTrue(finder.isWordChar(0));
        }
        for (int i = '0'; i <= '9'; i++) {
            XMLWordFinder finder = new XMLWordFinder(String.valueOf((char)i));
            assertTrue(finder.isWordChar(0));
        }
    }

    @Test
    void testIsWordChar_apostrophe() {
        // By itself, it is not a word char
        XMLWordFinder finder = new XMLWordFinder("'");
        assertFalse(finder.isWordChar(0));

        // In a contraction (in between letters), it is
        finder = new XMLWordFinder("can't");
        for (int i = 0; i < finder.getText().length(); i++) {
            assertTrue(finder.isWordChar(i));
        }

        // At the end of input, it isn't
        finder = new XMLWordFinder("can'");
        assertFalse(finder.isWordChar(finder.getText().length() - 1));

        // If in between a letter and a non-word char, it isn't
        finder = new XMLWordFinder("can'#");
        assertTrue(finder.isWordChar(0));
        assertTrue(finder.isWordChar(1));
        assertTrue(finder.isWordChar(2));
        assertFalse(finder.isWordChar(3));
        assertFalse(finder.isWordChar(4));
    }

    @Test
    void testIsWordChar_nonWordChar() {
        XMLWordFinder finder = new XMLWordFinder("#");
        assertFalse(finder.isWordChar(0));
    }
}
