package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Unit tests for {@link SpellDictionaryDichoDisk}.
 */
class SpellDictionaryDichoDiskTest {

    private SpellDictionaryDichoDisk dictionary;

    private static final String TEST_DIR_PREFIX = "scUnitTests_dichoDisk";
    private static final String[] CONTENT = {
            "ARTFRK*aardvark", "APL*apple", "PT*bat", "KT*cat", "TK*dog", "ALPNT*elephant"
    };

    /**
     * Utility method for test setup.
     */
    private static SpellDictionaryDichoDisk createDictionary(String[] content, String encoding) throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", content));
        return new SpellDictionaryDichoDisk(wordsFile, encoding);
    }

    @Test
    void testConstructor_oneArg() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryDichoDisk(wordsFile)
        );
    }

    @Test
    void testConstructor_twoArg_encoding() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryDichoDisk(wordsFile, "UTF-8")
        );
    }

    @Test
    void testConstructor_twoArg_phoneticFile() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryDichoDisk(wordsFile, (File)null)
        );
    }

    @Test
    void testConstructor_threeArg() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryDichoDisk(wordsFile, null, "UTF-8")
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testAddWord(String encoding) throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, encoding);
        Assertions.assertFalse(dictionary.addWord("newword"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testGetWords_existingWord(String encoding) throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, encoding);
        List<String> words = dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("dog"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testGetWords_nonExistingWord(String encoding) throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, encoding);
        List<String> words = dictionary.getWords(dictionary.getCode("nonexistent"));
        Assertions.assertTrue(words.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_correctWord(String encoding) throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, encoding);
        Assertions.assertTrue(dictionary.isCorrect("cat"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_correctWord_caseInsensitive(String encoding) throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, encoding);
        Assertions.assertTrue(dictionary.isCorrect("Cat"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_incorrectWord(String encoding) throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, encoding);
        Assertions.assertFalse(dictionary.isCorrect("wrongword"));
    }
}
