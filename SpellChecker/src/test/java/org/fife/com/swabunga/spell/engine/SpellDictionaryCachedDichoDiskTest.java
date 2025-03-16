package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Unit tests for {@link SpellDictionaryCachedDichoDisk}.
 */
class SpellDictionaryCachedDichoDiskTest {

    private SpellDictionaryCachedDichoDisk dictionary;

    private static final String TEST_DIR_PREFIX = "scUnitTests_dichoDisk";
    private static final String[] CONTENT = {
            "ARTFRK*aardvark", "APL*apple", "PT*bat", "KT*cat", "TK*dog", "ALPNT*elephant"
    };

    private static void recursiveDelete(File dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    private static File createWordsFile(String[] content) throws IOException {
        File precacheDir = new File(SpellDictionaryCachedDichoDisk.getPreCacheDir());
        if (precacheDir.isDirectory()) {
            recursiveDelete(precacheDir);
        }

        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        wordsFile.deleteOnExit();
        Files.writeString(wordsFile.toPath(), String.join("\n", content));
        return wordsFile;
    }

    /**
     * Utility method for test setup.
     */
    private static SpellDictionaryCachedDichoDisk createDictionary_noCache(String[] content, String encoding)
            throws IOException {
        return new SpellDictionaryCachedDichoDisk(createWordsFile(content), encoding);
    }

    /**
     * Utility method for test setup.
     */
    private static SpellDictionaryCachedDichoDisk createDictionary_noCache(String[] content, String encoding,
                                                                           int maxCacheSize) throws IOException {
        return new SpellDictionaryCachedDichoDisk(createWordsFile(content), encoding, maxCacheSize);
    }

    @Test
    void testConstructor_oneArg() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryCachedDichoDisk(wordsFile)
        );
    }

    @Test
    void testConstructor_twoArg_encoding() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryCachedDichoDisk(wordsFile, "UTF-8")
        );
    }

    @Test
    void testConstructor_twoArg_phoneticFile() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryCachedDichoDisk(wordsFile, (File)null)
        );
    }

    @Test
    void testConstructor_threeArg() throws IOException {
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        Assertions.assertDoesNotThrow(() ->
            dictionary = new SpellDictionaryCachedDichoDisk(wordsFile, null, "UTF-8")
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testAddWord(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        Assertions.assertFalse(dictionary.addWord("newword"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testClearCache(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(1, dictionary.getCacheSize());
        Assertions.assertEquals(0, dictionary.getHits());

        dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(1, dictionary.getCacheSize());
        Assertions.assertEquals(1, dictionary.getHits());

        dictionary.clearCache();
        Assertions.assertEquals(0, dictionary.getCacheSize());
        Assertions.assertEquals(0, dictionary.getHits());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testGetWords_existingWord(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        List<String> words = dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("dog"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testGetWords_existingWord_cached(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        List<String> words = dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("dog"));
        Assertions.assertEquals(0, dictionary.getHits());

        words = dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("dog"));
        Assertions.assertEquals(1, dictionary.getHits());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testGetWords_nonExistingWord(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        List<String> words = dictionary.getWords(dictionary.getCode("nonexistent"));
        Assertions.assertTrue(words.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testGetWords_cacheSpillover(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding, 2);

        List<String> words = dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("dog"));

        words = dictionary.getWords(dictionary.getCode("cat"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("cat"));

        words = dictionary.getWords(dictionary.getCode("cat"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("cat"));

        words = dictionary.getWords(dictionary.getCode("apple"));
        Assertions.assertEquals(1, words.size());
        Assertions.assertTrue(words.contains("apple"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_correctWord(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        Assertions.assertTrue(dictionary.isCorrect("cat"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_correctWord_caseInsensitive(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        Assertions.assertTrue(dictionary.isCorrect("Cat"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "UTF-8")
    void testIsCorrect_incorrectWord(String encoding) throws IOException {
        SpellDictionaryCachedDichoDisk dictionary = createDictionary_noCache(CONTENT, encoding);
        Assertions.assertFalse(dictionary.isCorrect("wrongword"));
    }

    @Test
    void testSaveLoadCache() throws IOException {
        // Load once to create the cache
        File wordsFile = File.createTempFile(TEST_DIR_PREFIX, ".txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", CONTENT));
        dictionary = new SpellDictionaryCachedDichoDisk(wordsFile);
        Assertions.assertEquals(0, dictionary.getCacheSize());

        dictionary.getWords(dictionary.getCode("apple"));
        dictionary.getWords(dictionary.getCode("dog"));
        Assertions.assertEquals(2, dictionary.getCacheSize());
        dictionary.saveCache();

        // Load again to use the cache
        dictionary = new SpellDictionaryCachedDichoDisk(wordsFile);
        Assertions.assertEquals(2, dictionary.getCacheSize());
    }
}
