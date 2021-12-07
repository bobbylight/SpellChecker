package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit tests for the {@code SpellChecker} class.
 */
class SpellCheckerTest {

    @Test
    void testSplitMixedCaseWord_happyPath_notCamelCaseWord() {

        List<String> actual = SpellChecker.splitMixedCaseWord("archer");
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("archer", actual.get(0));
    }

    @Test
    void testSplitMixedCaseWord_happyPath_allValidWords() {

        List<String> actual = SpellChecker.splitMixedCaseWord("camelCaseWord");
        Assertions.assertEquals(3, actual.size());
        Assertions.assertEquals("camel", actual.get(0));
        Assertions.assertEquals("Case", actual.get(1));
        Assertions.assertEquals("Word", actual.get(2));
    }

    @Test
    void testSplitMixedCaseWord_happyPath_oneWordWithAllCaps() {

        List<String> actual = SpellChecker.splitMixedCaseWord("HTMLValidator");
        Assertions.assertEquals(2, actual.size(),
                "Unexpected split word count, actual = " + String.join(", ", actual));
        Assertions.assertEquals("HTML", actual.get(0));
        Assertions.assertEquals("Validator", actual.get(1));
    }

    @Test
    void testSplitMixedCaseWord_happyPath_multipleWordsWithAllCaps() {

        List<String> actual = SpellChecker.splitMixedCaseWord("HTMLToXMLValidator");
        Assertions.assertEquals(4, actual.size(),
                "Unexpected split word count, actual = " + String.join(", ", actual));
        Assertions.assertEquals("HTML", actual.get(0));
        Assertions.assertEquals("To", actual.get(1));
        Assertions.assertEquals("XML", actual.get(2));
        Assertions.assertEquals("Validator", actual.get(3));
    }

    @Test
    void testSplitMixedCaseWord_happyPath_multipleWordsWithAllCaps2() {

        List<String> actual = SpellChecker.splitMixedCaseWord("weDONTWantTODoTHIS");
        Assertions.assertEquals(6, actual.size(),
                "Unexpected split word count, actual = " + String.join(", ", actual));
        Assertions.assertEquals("we", actual.get(0));
        Assertions.assertEquals("DONT", actual.get(1));
        Assertions.assertEquals("Want", actual.get(2));
        Assertions.assertEquals("TO", actual.get(3));
        Assertions.assertEquals("Do", actual.get(4));
        Assertions.assertEquals("THIS", actual.get(5));
    }
}
