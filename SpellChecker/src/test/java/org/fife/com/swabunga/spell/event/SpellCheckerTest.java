package org.fife.com.swabunga.spell.event;

import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.engine.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code SpellChecker} class.
 */
class SpellCheckerTest {

    private SpellChecker checker;

    @BeforeEach
    void setUp() {
        checker = new SpellChecker();
    }

    private void addWordsToDictionary(String[] words) {
        try {
            checker.addDictionary(
                    new SpellDictionaryHashMap(new StringReader(String.join("\n", words)))
            );
        } catch (IOException ioe) {
            ioe.printStackTrace(); // Tests will fail
        }
    }

    @Test
    void testCheckSpelling_noErrors() {
        String[] words = { "This", "is", "a", "correctly", "spelled", "sentence" };
        addWordsToDictionary(words);

        String text = String.join(" ", words) + ".";
        StringWordTokenizer tokenizer = new StringWordTokenizer(text);
        int result = checker.checkSpelling(tokenizer);
        assertEquals(SpellChecker.SPELLCHECK_OK, result);
    }

    @Test
    void testCheckSpelling_withErrors() {
        String[] words = { "This", "is", "a", "sentence", "with", "errors" };
        addWordsToDictionary(words);

        String text = "Ths is a sentence with erors.";
        StringWordTokenizer tokenizer = new StringWordTokenizer(text);
        int result = checker.checkSpelling(tokenizer);
        assertEquals(2, result);
    }

    @Test
    void testGetSuggestions() {
        String[] words = { "This", "is", "a", "sentence", "with", "errors" };
        addWordsToDictionary(words);

        List<Word> suggestions = checker.getSuggestions("erors", 0);
        assertEquals(1, suggestions.size());
        assertEquals("errors", suggestions.get(0).getWord());
    }

    @Test
    void testSetUserDictionary() {
        assertDoesNotThrow(() -> {
            checker.setUserDictionary(new SpellDictionaryHashMap());
        });
    }

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
