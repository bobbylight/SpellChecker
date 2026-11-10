package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;

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
        Assertions.assertEquals("aardvark", lines.getFirst());
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
        Assertions.assertEquals("foobar", lines.getFirst());
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
        Assertions.assertEquals("foobar", lines.getFirst());
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
        Assertions.assertEquals("foobar", lines.getFirst());
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
        Assertions.assertEquals("added", lines.getFirst());
    }

    @Test
    void testIsCorrect_capitalizedWord_independentOfDefaultLocale() throws IOException {
        Locale orig = Locale.getDefault();
        try {
            // Turkish lowercases 'I' to a dotless i
            Locale.setDefault(Locale.of("tr", "TR"));
            SpellDictionaryHashMap dic = new SpellDictionaryHashMap(new StringReader("item\nidle\n"));
            Assertions.assertTrue(dic.isCorrect("Item"));
            Assertions.assertTrue(dic.isCorrect("Idle"));
        } finally {
            Locale.setDefault(orig);
        }
    }

    @Test
    void testConstructor_file_readsUtf8() throws IOException {
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        wordFile.deleteOnExit();
        Files.write(wordFile.toPath(), "caf\u00e9\n".getBytes(StandardCharsets.UTF_8));

        SpellDictionaryHashMap dic = new SpellDictionaryHashMap(wordFile);
        Assertions.assertTrue(dic.isCorrect("caf\u00e9"));
    }

    @Test
    void testAddWord_file_writesUtf8() throws IOException {
        File wordFile = File.createTempFile("scUnitTest", ".dic");
        wordFile.deleteOnExit();

        SpellDictionaryHashMap dic = new SpellDictionaryHashMap(wordFile);
        Assertions.assertTrue(dic.addWord("na\u00efve"));

        byte[] expected = "na\u00efve\n".getBytes(StandardCharsets.UTF_8);
        Assertions.assertArrayEquals(expected, Files.readAllBytes(wordFile.toPath()));
    }

    @Test
    void testGetWords_unmodifiable() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        List<String> words = dic.getWords(dic.getCode("test"));
        Assertions.assertTrue(words.contains("test"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> words.add("foo"));
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

        List<Word> suggestions = dic.getSuggestions("change", Configuration.getConfiguration());
        Assertions.assertEquals(2, suggestions.size());
        Assertions.assertEquals("change", suggestions.get(0).getWord());
        Assertions.assertEquals("chance", suggestions.get(1).getWord());
    }

    @Test
    void testGetSuggestions_respectsThreshold() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        Configuration config = Configuration.getConfiguration();
        config.setInteger(Configuration.SPELL_THRESHOLD, 1); // Only exact matches

        List<Word> suggestions = dic.getSuggestions("change", config);
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("change", suggestions.getFirst().getWord());
    }

    @Test
    void testGetSuggestions_respectsEditCosts() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();
        Configuration config = Configuration.getConfiguration();
        // Make "chance" (one substitution away) cost more than the threshold
        config.setInteger(Configuration.COST_SUBST_CHARS, 1000);

        List<Word> suggestions = dic.getSuggestions("change", config);
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("change", suggestions.getFirst().getWord());
    }

    @Test
    void testGetSuggestions_nonExistingWord() throws IOException {
        SpellDictionaryHashMap dic = createDictionary_zeroArg();

        List<Word> suggestions = dic.getSuggestions("changex", Configuration.getConfiguration());
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("change", suggestions.getFirst().getWord());
    }

    @Test
    void testGetSuggestions_extraLetterChangesPhoneticCode() throws IOException {
        SpellDictionaryHashMap dic = new SpellDictionaryHashMap(new StringReader("school\nsatchel\n"));

        // The extra letter gives these a different phonetic code from the
        // intended word, so only deleting a letter finds it
        List<Word> suggestions = dic.getSuggestions("schotol", Configuration.getConfiguration());
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("school", suggestions.getFirst().getWord());

        suggestions = dic.getSuggestions("satrchel", Configuration.getConfiguration());
        Assertions.assertEquals(1, suggestions.size());
        Assertions.assertEquals("satchel", suggestions.getFirst().getWord());
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
