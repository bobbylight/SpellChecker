package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SpellDictionaryDisk}.
 */
class SpellDictionaryDiskTest {

    private SpellDictionaryDisk dictionary;
    private static final String[] WORDS = { "aardvark", "apple", "bat" };

    @BeforeEach
    void setUp() throws IOException {
        Path tempDir = Files.createTempDirectory("scUnitTests");
        File wordsDir = new File(tempDir.toFile(), "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", WORDS));

        dictionary = new SpellDictionaryDisk(tempDir.toFile(), null, true);
        assertTrue(dictionary.isReady());
    }

    @Test
    void testConstructor_async() throws IOException, InterruptedException {
        Path tempDir = Files.createTempDirectory("scUnitTests");
        File wordsDir = new File(tempDir.toFile(), "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", WORDS));

        dictionary = new SpellDictionaryDisk(tempDir.toFile(), null, false);
        Thread.sleep(1000);
        assertTrue(dictionary.isReady());
    }

    @Test
    void testConstructor_existingDatabaseSetup() throws IOException {
        Path tempDir = Files.createTempDirectory("scUnitTests");
        File wordsDir = new File(tempDir.toFile(), "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", WORDS));

        dictionary = new SpellDictionaryDisk(tempDir.toFile(), null, true);
        assertTrue(dictionary.isReady());

    }

    @Test
    void testSetup_baseDirDoesNotExist() {
        assertThrows(FileNotFoundException.class, () ->
            new SpellDictionaryDisk(new File("doesNotExist"), null, true)
        );
    }

    @Test
    void testSetup_wordsSubdirDoesNotExist() throws IOException {
        Path baseDir = Files.createTempDirectory("scUnitTests");
        assertThrows(FileNotFoundException.class, () ->
            new SpellDictionaryDisk(baseDir.toFile(), null, true)
        );
    }

    @Test
    void testSetup_dbSubdirExists() throws IOException {
        File baseDir = Files.createTempDirectory("scUnitTests").toFile();
        assertTrue(new File(baseDir, "words").mkdirs());
        assertTrue(new File(baseDir, "db").mkdirs());
        File dbFile = new File(baseDir, "db/words.idx");
        assertTrue(dbFile.createNewFile());
        dbFile.deleteOnExit();
        SpellDictionaryDisk sdd = new SpellDictionaryDisk(baseDir, null, true);
        assertTrue(sdd.isReady());
    }

    @Test
    void testAddWord() {
        assertThrows(UnsupportedOperationException.class, () ->
            dictionary.addWord("testword")
        );
    }

    @Test
    void testGetWords() {
        List<String> words = dictionary.getWords("APL");
        assertEquals(1, words.size());
        assertTrue(words.contains("apple"));
    }

    @Test
    void testIsCorrect_wordNotFound() {
        assertFalse(dictionary.isCorrect("incorrectword"));
    }

    @Test
    void testIsCorrect_wordFound() {
        assertTrue(dictionary.isCorrect("apple"));
    }
}
