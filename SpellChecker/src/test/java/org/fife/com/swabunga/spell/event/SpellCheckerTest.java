package org.fife.com.swabunga.spell.event;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for the {@code SpellChecker} class.
 */
public class SpellCheckerTest {

    @Test
    public void testSplitMixedCaseWord_happyPath_notCamelCaseWord() {

        List<String> actual = SpellChecker.splitMixedCaseWord("archer");
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals("archer", actual.get(0));
    }

    @Test
    public void testSplitMixedCaseWord_happyPath_allValidWords() {

        List<String> actual = SpellChecker.splitMixedCaseWord("camelCaseWord");
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals("camel", actual.get(0));
        Assert.assertEquals("Case", actual.get(1));
        Assert.assertEquals("Word", actual.get(2));
    }

    @Test
    public void testSplitMixedCaseWord_happyPath_oneWordWithAllCaps() {

        List<String> actual = SpellChecker.splitMixedCaseWord("HTMLValidator");
        Assert.assertEquals("Unexpected split word count, actual = " + String.join(", ", actual), 2, actual.size());
        Assert.assertEquals("HTML", actual.get(0));
        Assert.assertEquals("Validator", actual.get(1));
    }

    @Test
    public void testSplitMixedCaseWord_happyPath_multipleWordsWithAllCaps() {

        List<String> actual = SpellChecker.splitMixedCaseWord("HTMLToXMLValidator");
        Assert.assertEquals("Unexpected split word count, actual = " + String.join(", ", actual), 4, actual.size());
        Assert.assertEquals("HTML", actual.get(0));
        Assert.assertEquals("To", actual.get(1));
        Assert.assertEquals("XML", actual.get(2));
        Assert.assertEquals("Validator", actual.get(3));
    }

    @Test
    public void testSplitMixedCaseWord_happyPath_multipleWordsWithAllCaps2() {

        List<String> actual = SpellChecker.splitMixedCaseWord("weDONTWantTODoTHIS");
        Assert.assertEquals("Unexpected split word count, actual = " + String.join(", ", actual), 6, actual.size());
        Assert.assertEquals("we", actual.get(0));
        Assert.assertEquals("DONT", actual.get(1));
        Assert.assertEquals("Want", actual.get(2));
        Assert.assertEquals("TO", actual.get(3));
        Assert.assertEquals("Do", actual.get(4));
        Assert.assertEquals("THIS", actual.get(5));
    }
}
