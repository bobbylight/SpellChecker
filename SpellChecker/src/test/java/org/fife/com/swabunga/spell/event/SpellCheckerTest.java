package org.fife.com.swabunga.spell.event;

import org.fife.com.swabunga.spell.engine.SpellDictionary;
import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.engine.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code SpellChecker} class.
 */
class SpellCheckerTest {

    private SpellChecker checker;
    private TestSpellCheckListener listener = new TestSpellCheckListener();

    private static final String TEMP_FILE_PREFIX = "scUnitTests_spellChecker";

    @BeforeEach
    void setUp() throws IOException{
        checker = new SpellChecker(new SpellDictionaryHashMap());
        checker.addSpellCheckListener(listener);
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
    void testConstructor_oneArg() {
        assertDoesNotThrow(() -> new SpellChecker(new SpellDictionaryHashMap()));
    }

    @Test
    void testConstructor_oneArg_errorOnNull() {
        assertThrows(IllegalArgumentException.class, () -> new SpellChecker(null));
    }

    @Test
    void testAddDictionary_errorOnNull() {
        assertThrows(IllegalArgumentException.class, () -> checker.addDictionary(null));
    }

    @Test
    void testAddRemoveSpellCheckListener() {
        List<SpellCheckEvent> events = new ArrayList<>();
        SpellCheckListener listener = events::add;

        // Doesn't receive events before being added
        SpellCheckEvent e1 = new BasicSpellCheckEvent("xxx", 0);
        checker.fireAndHandleEvent(e1);
        assertTrue(events.isEmpty());

        // Receives events after being added
        checker.addSpellCheckListener(listener);
        SpellCheckEvent e2 = new BasicSpellCheckEvent("yyy", 0);
        checker.fireAndHandleEvent(e2);
        assertEquals(1, events.size());
        assertEquals(e2, events.get(0));

        // Doesn't receive events after being removed
        checker.removeSpellCheckListener(listener);
        SpellCheckEvent e3 = new BasicSpellCheckEvent("zzz", 0);
        checker.fireAndHandleEvent(e3);
        assertEquals(1, events.size());
    }

    @Test
    void testAddToDictionary_dictionaryFileOnDisk() throws IOException {
        File wordList = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
        wordList.deleteOnExit();
        checker.setUserDictionary(new SpellDictionaryHashMap(wordList));

        assertTrue(checker.addToDictionary("foo"));
        assertFalse(checker.addToDictionary("foo")); // Can't add a second time
    }

    @Test
    void testAddToDictionary_noFileOnDisk() {
        // Always returns false if no backing file
        assertFalse(checker.addToDictionary("foo"));
    }

    @Test
    void testBeginsAsINETWord() {
        assertTrue(SpellChecker.beginsAsINETWord("http://google.com"));
        assertTrue(SpellChecker.beginsAsINETWord("https://google.com"));
        assertTrue(SpellChecker.beginsAsINETWord("www.google.com"));
        assertTrue(SpellChecker.beginsAsINETWord("ftp://google.com"));
        assertTrue(SpellChecker.beginsAsINETWord("ftps://google.com"));
        assertFalse(SpellChecker.beginsAsINETWord("help@google.com"));
        assertFalse(SpellChecker.beginsAsINETWord("google.com"));
    }

    @Test
    void testIsINETWord() {
        assertTrue(SpellChecker.isINETWord("http://google.com"));
        assertTrue(SpellChecker.isINETWord("https://google.com"));
        assertTrue(SpellChecker.isINETWord("www.google.com"));
        assertTrue(SpellChecker.isINETWord("ftp://google.com"));
        assertTrue(SpellChecker.isINETWord("ftps://google.com"));
        assertTrue(SpellChecker.isINETWord("help@google.com"));
        assertFalse(SpellChecker.isINETWord("google.com"));
    }

    @Test
    void testIsCorrect_nonUserDictionaryREturnsTrue() throws IOException {
        SpellDictionary newDictionary = new SpellDictionaryHashMap();
        newDictionary.addWord("foo");
        checker.addDictionary(newDictionary);
        assertTrue(checker.isCorrect("foo"));
    }

    @Test
    void testIsCorrect_userDictionaryReturnsTrue() throws IOException {
        SpellDictionary userDictionary = new SpellDictionaryHashMap();
        userDictionary.addWord("foo");
        checker.setUserDictionary(userDictionary);
        assertTrue(checker.isCorrect("foo"));
    }

    @Test
    void testIsCorrect_noDictionaryContainsWord() {
        assertFalse(checker.isCorrect("unknownWord"));
    }

    @Test
    void testCheckSpelling_noErrors_happyPath() {
        String[] words = { "This", "is", "a", "correctly", "spelled", "sentence" };
        addWordsToDictionary(words);

        String text = String.join(" ", words) + ".";
        StringWordTokenizer tokenizer = new StringWordTokenizer(text);
        int result = checker.checkSpelling(tokenizer);
        assertEquals(SpellChecker.SPELLCHECK_OK, result);
    }

    @Test
    void testCheckSpelling_noErrors_mixedCaseWord() {
        String[] words = { "This", "is", "a", "correctly", "spelled", "sentence" };
        addWordsToDictionary(words);

        String text = "This is a correctlySpelledSentence.";
        StringWordTokenizer tokenizer = new StringWordTokenizer(text);
        int result = checker.checkSpelling(tokenizer);
        assertEquals(SpellChecker.SPELLCHECK_OK, result);
    }

    @Test
    void testCheckSpelling_withErrors_happyPath() {
        String[] words = { "This", "is", "a", "sentence", "with", "errors" };
        addWordsToDictionary(words);

        String text = "Ths is a sentence with erors.";
        StringWordTokenizer tokenizer = new StringWordTokenizer(text);
        int result = checker.checkSpelling(tokenizer);
        assertEquals(2, result);

        List<SpellCheckEvent> events = listener.getEvents();
        assertEquals(2, events.size());

        SpellCheckEvent e = listener.events.get(0);
        assertEquals("Ths", e.getInvalidWord());
        assertEquals(0, e.getWordContextPosition());

        e = listener.events.get(1);
        assertEquals("erors", e.getInvalidWord());
        assertEquals(23, e.getWordContextPosition());
    }

    @Test
    void testCheckSpelling_withErrors_mixedCaseWord() {
        String[] words = { "This", "is", "a", "correctly", "spelled", "sentence" };
        addWordsToDictionary(words);

        String text = "This is a correktlySpellledSentence.";
        StringWordTokenizer tokenizer = new StringWordTokenizer(text);
        int result = checker.checkSpelling(tokenizer);
        assertEquals(2, result);

        List<SpellCheckEvent> events = listener.getEvents();
        assertEquals(2, events.size());

        SpellCheckEvent e = listener.events.get(0);
        assertEquals("correktly", e.getInvalidWord());
        assertEquals(10, e.getWordContextPosition());

        e = listener.events.get(1);
        assertEquals("Spellled", e.getInvalidWord());
        assertEquals(19, e.getWordContextPosition());
    }

    @Test
    void testFireAndHandleEvent() {
        SpellCheckEvent e = new BasicSpellCheckEvent("invalidWord", 0);
        assertFalse(checker.fireAndHandleEvent(e));
    }

    @Test
    void testGetConfiguration() {
        assertNotNull(checker.getConfiguration());
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
    void testIgnoreWords() {
        String[] words = { "This", "is", "a", "sentence", "with", "errors" };
        addWordsToDictionary(words);
        checker.ignoreAll("erors");

        // Getter works
        Assertions.assertTrue(checker.isIgnored("erors"));

        // Checker skips the word as well
        String text = "This is a sentence with erors.";
        StringWordTokenizer tokenizer = new StringWordTokenizer(text);
        int result = checker.checkSpelling(tokenizer);
        assertEquals(-1, result);
    }

    @Test
    void testSetCache() {
        assertDoesNotThrow(() -> {
            checker.setCacheSize(0);
            checker.setCacheSize(300);
        });
    }

    @Test
    void testSetUserDictionary() {
        assertDoesNotThrow(() -> checker.setUserDictionary(new SpellDictionaryHashMap()));
    }

    @Test
    void testSetUserDictionary_throwsIfNull() {
        assertThrows(IllegalArgumentException.class, () -> checker.setUserDictionary(null));
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

    /**
     * Used to verify callback behavior.
     */
    private static final class TestSpellCheckListener implements SpellCheckListener {

        private List<SpellCheckEvent> events = new ArrayList<>();

        private List<SpellCheckEvent> getEvents() {
            return Collections.unmodifiableList(events);
        }

        @Override
        public boolean spellingError(SpellCheckEvent event) {
            events.add(event);
            return false;
        }
    }
}
