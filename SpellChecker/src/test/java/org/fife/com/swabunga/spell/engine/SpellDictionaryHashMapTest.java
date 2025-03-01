package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.List;

/**
 * Unit tests for {@link SpellDictionaryHashMap}.
 */
class SpellDictionaryHashMapTest {

    private static final String[] WORDS = { "chance", "chanced", "chances", "change", "test", "example" };

    private SpellDictionaryHashMap createDictionary_zeroArg() throws IOException {
        // Initialize the dictionary with some test data
        SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap();
        for (String word: WORDS) {
            dictionary.addWord(word);
        }
        return dictionary;
    }

    @Test
    void testConstructor_zeroArg() {
        Assertions.assertDoesNotThrow(() -> new SpellDictionaryHashMap());
    }

    @Test
    void testConstructor_singleArg_reader() throws IOException{
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        Files.writeString(wordFile.toPath(), "aardvark\n");
        wordFile.deleteOnExit();

        wordFile.deleteOnExit();
        SpellDictionaryHashMap dictionary;
        try (FileReader r = new FileReader(wordFile)) {
            dictionary = new SpellDictionaryHashMap(r);
        }

        // Adding a word does not it to the file, since the file wasn't passed in.
        dictionary.addWord("foobar");
        List<String> lines = Files.readAllLines(wordFile.toPath());
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("aardvark", lines.get(0));
    }

    @Test
    void testConstructor_singleArg_file() throws IOException{
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        wordFile.deleteOnExit();
        SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap(wordFile);

        // Adding a word persists it to the file.
        dictionary.addWord("foobar");
        List<String> lines = Files.readAllLines(wordFile.toPath());
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("foobar", lines.get(0));
    }

    @Test
    void testConstructor_twoArg_twoFiles() throws IOException{
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        wordFile.deleteOnExit();
        SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap(wordFile, null);

        // Adding a word persists it to the file.
        dictionary.addWord("foobar");
        List<String> lines = Files.readAllLines(wordFile.toPath());
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("foobar", lines.get(0));
    }

    @Test
    void testConstructor_threeArg() throws IOException{
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        wordFile.deleteOnExit();
        SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap(wordFile, null, null);

        // Adding a word persists it to the file.
        dictionary.addWord("foobar");
        List<String> lines = Files.readAllLines(wordFile.toPath());
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("foobar", lines.get(0));
    }

    @Test
    void testConstructor_twoArg_twoReaders() throws IOException{
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        wordFile.deleteOnExit();
        SpellDictionaryHashMap dictionary;
        try (FileReader r = new FileReader(wordFile)) {
            dictionary = new SpellDictionaryHashMap(r, null);
        }

        // Adding a word does not it to the file, since the file wasn't passed in.
        dictionary.addWord("foobar");
        List<String> lines = Files.readAllLines(wordFile.toPath());
        Assertions.assertEquals(0, lines.size());
    }

    @Test
    void testAddDictionary() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        Assertions.assertFalse(dic.isCorrect("added"));

        StringReader r = new StringReader("added");
        dic.addDictionary(r);
        Assertions.assertTrue(dic.isCorrect("added"));
    }

    @Test
    void testAddWord_returnsFalseIfNoFile() throws IOException {
        SpellDictionaryHashMap dic = new SpellDictionaryHashMap();
        Assertions.assertFalse(dic.addWord("added"));
    }

    @Test
    void testAddWord_returnsTrueIfFile() throws IOException {
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        wordFile.deleteOnExit();
        SpellDictionaryHashMap dic = new SpellDictionaryHashMap(wordFile);
        Assertions.assertTrue(dic.addWord("added"));

        // Verify the word was actually added to the file
        List<String> lines = Files.readAllLines(wordFile.toPath());
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals("added", lines.get(0));
    }

    @Test
    void testPutWordUnique_nullMainDictionary() throws IOException{
        SpellDictionaryHashMap dic = new SpellDictionaryHashMap();
        Assertions.assertFalse(dic.isCorrect("test"));
        dic.putWordUnique("test");
        Assertions.assertTrue(dic.isCorrect("test"));

    }

    @Test
    void testPutWordUnique_nonNullMainDictionary() throws IOException{
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        Files.writeString(wordFile.toPath(), "aardvark\n");
        wordFile.deleteOnExit();

        SpellDictionaryHashMap dic = new SpellDictionaryHashMap(wordFile);
        Assertions.assertFalse(dic.isCorrect("test"));
        dic.putWordUnique("test");
        Assertions.assertTrue(dic.isCorrect("test"));
        Assertions.assertTrue(dic.isCorrect("aardvark")); // sanity

        dic.putWordUnique("test");
        Assertions.assertTrue(dic.isCorrect("test"));
    }

    @Test
    void testGetSuggestions_existingWord() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();

        // Note: threshold is unused in SpellDictionaryASpell (!!)
        List<Word> suggestions = dic.getSuggestions("change", 0);
        Assertions.assertEquals(2, suggestions.size());
        Assertions.assertEquals("change", suggestions.get(0).getWord());
        Assertions.assertEquals("chance", suggestions.get(1).getWord());
    }

    @Test
    void testGetSuggestions_nonExistingWord() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();

        // Note: threshold is unused in SpellDictionaryASpell (!!)
        List<Word> suggestions = dic.getSuggestions("changex", 0);
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("change", suggestions.get(0).getWord());
    }

    @Test
    void testGetWords_existingWord() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        List<String> words = dic.getWords(dic.getCode("test"));
        Assertions.assertTrue(words.contains("test"));
    }

    @Test
    void testGetWords_nonExistingWord() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        List<String> words = dic.getWords(dic.getCode("nonexistent"));
        Assertions.assertTrue(words.isEmpty());
    }

    @Test
    void testIsCorrect_correctWord() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        Assertions.assertTrue(dic.isCorrect("test"));
    }

    @Test
    void testIsCorrect_incorrectWord() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        Assertions.assertFalse(dic.isCorrect("wrongword"));
    }

    @Test
    void testIsCorrect_caseInsensitive() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        Assertions.assertTrue(dic.isCorrect("Test"));
    }
}
