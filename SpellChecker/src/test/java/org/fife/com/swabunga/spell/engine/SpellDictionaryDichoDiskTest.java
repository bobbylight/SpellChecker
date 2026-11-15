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
import java.util.stream.Collectors;

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
        return createDictionary(String.join("\n", content), encoding);
    }

    private static final List<String> WORDS = List.of("aardvark", "apple", "bat", "cat", "dog", "elephant");

    /**
     * Returns "code*word" lines for words, using their real phonetic codes
     * and sorted by code, as the dictionary's binary search requires.
     */
    private static List<String> sortedLines(List<String> words) throws IOException {
        File empty = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        empty.deleteOnExit();
        try (SpellDictionaryDichoDisk probe = new SpellDictionaryDichoDisk(empty)) {
            return words.stream()
                .map(word -> probe.getCode(word) + "*" + word)
                .sorted()
                .collect(Collectors.toList());
        }
    }

    private static SpellDictionaryDichoDisk createDictionary(String content, String encoding) throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        wordsFile.deleteOnExit();
        Files.writeString(wordsFile.toPath(), content);
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

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_lastLineWithoutNewline(String encoding) throws IOException {
        String content = String.join("\n", sortedLines(WORDS));
        try (SpellDictionaryDichoDisk dictionary = createDictionary(content, encoding)) {
            for (String word : WORDS) {
                Assertions.assertTrue(dictionary.isCorrect(word), word);
            }
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_crlfLineEndings(String encoding) throws IOException {
        String content = String.join("\r\n", sortedLines(WORDS)) + "\r\n";
        try (SpellDictionaryDichoDisk dictionary = createDictionary(content, encoding)) {
            for (String word : WORDS) {
                Assertions.assertTrue(dictionary.isCorrect(word), word);
            }
            Assertions.assertFalse(dictionary.isCorrect("wrongword"));
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_lfLineEndings(String encoding) throws IOException {
        String content = String.join("\n", sortedLines(WORDS)) + "\n";
        try (SpellDictionaryDichoDisk dictionary = createDictionary(content, encoding)) {
            for (String word : WORDS) {
                Assertions.assertTrue(dictionary.isCorrect(word), word);
            }
            Assertions.assertFalse(dictionary.isCorrect("wrongword"));
        }
    }

    @Test
    void testIsCorrect_lineLongerThan255Bytes() throws IOException {
        List<String> words = List.of("apple", "a".repeat(300), "dog");
        String content = String.join("\n", sortedLines(words)) + "\n";
        try (SpellDictionaryDichoDisk dictionary = createDictionary(content, "UTF-8")) {
            for (String word : words) {
                Assertions.assertTrue(dictionary.isCorrect(word), word);
            }
        }
    }

    @Test
    void testIsCorrect_nonAsciiWord_utf8() throws IOException {
        List<String> words = List.of("apple", "caf\u00e9", "dog");
        String content = String.join("\n", sortedLines(words)) + "\n";
        try (SpellDictionaryDichoDisk dictionary = createDictionary(content, "UTF-8")) {
            Assertions.assertTrue(dictionary.isCorrect("caf\u00e9"));
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testClose(String encoding) throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, encoding);
        Assertions.assertTrue(dictionary.isCorrect("cat"));
        dictionary.close();
        Assertions.assertThrows(IllegalStateException.class, () -> dictionary.isCorrect("cat"));
    }

    @Test
    void testClose_twice() throws IOException {
        SpellDictionaryDichoDisk dictionary = createDictionary(CONTENT, null);
        dictionary.close();
        Assertions.assertDoesNotThrow(dictionary::close);
    }
}
