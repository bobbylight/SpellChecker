package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

    private static final String TEST_DIR_PREFIX = "scUnitTests";

    @BeforeEach
    void setUp() throws IOException {
        File tempDir = Files.createTempDirectory(TEST_DIR_PREFIX).toFile();
        File wordsDir = new File(tempDir, "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", WORDS));

        dictionary = new SpellDictionaryDisk(tempDir, null, true);
        assertTrue(dictionary.isReady());
    }

    @Test
    void testConstructor_async() throws IOException, InterruptedException {
        File tempDir = Files.createTempDirectory(TEST_DIR_PREFIX).toFile();
        File wordsDir = new File(tempDir, "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", WORDS));

        dictionary = new SpellDictionaryDisk(tempDir, null, false);
        Thread.sleep(1000); // Allow time for the thread to complete
        assertTrue(dictionary.isReady());
    }

    @Test
    void testConstructor_sync_wordsDirExists_noContentFile() throws IOException {
        File tempDir = Files.createTempDirectory(TEST_DIR_PREFIX).toFile();
        File wordsDir = new File(tempDir, "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        Files.writeString(wordsFile.toPath(), String.join("\n", WORDS));

        dictionary = new SpellDictionaryDisk(tempDir, null, true);
        assertTrue(dictionary.isReady());

    }

    @Test
    @Disabled("Need to copy DB and index file for this test to succeed")
    void testConstructor_sync_wordsDirExists_contentsFileExists_noChanges() throws IOException {
        File tempDir = Files.createTempDirectory(TEST_DIR_PREFIX).toFile();

        // Create the words file.
        File wordsDir = new File(tempDir, "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        String wordsFileContent = String.join("\n", WORDS);
        Files.writeString(wordsFile.toPath(), wordsFileContent);

        // Create the "contents" cache file with the proper format.
        File dbDir = new File(tempDir, "db");
        assertTrue(dbDir.mkdirs());
        File contentsFile = new File(dbDir, "contents");
        // The word file content is now > 1 char, so this will trigger the DB regeneration code path
        Files.writeString(contentsFile.toPath(), "words.txt," + wordsFileContent.length());

        dictionary = new SpellDictionaryDisk(tempDir, null, true);
        assertTrue(dictionary.isReady());

    }

    @Test
    void testConstructor_sync_wordsDirExists_contentsFileExists_fileSizeChanged() throws IOException {
        File tempDir = Files.createTempDirectory(TEST_DIR_PREFIX).toFile();

        // Create the words file.
        File wordsDir = new File(tempDir, "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        String wordsFileContent = String.join("\n", WORDS);
        Files.writeString(wordsFile.toPath(), wordsFileContent);

        // Create the "contents" cache file with the proper format.
        File dbDir = new File(tempDir, "db");
        assertTrue(dbDir.mkdirs());
        File contentsFile = new File(dbDir, "contents");
        // The word file content is now > 1 char, so this will trigger the DB regeneration code path
        Files.writeString(contentsFile.toPath(), "words.txt,1");

        dictionary = new SpellDictionaryDisk(tempDir, null, true);
        assertTrue(dictionary.isReady());

    }

    @Test
    void testConstructor_sync_wordsDirExists_contentsFileExists_newFileAdded() throws IOException {
        File tempDir = Files.createTempDirectory(TEST_DIR_PREFIX).toFile();

        // Create the words file.
        File wordsDir = new File(tempDir, "words");
        assertTrue(wordsDir.mkdirs());
        File wordsFile = new File(wordsDir, "words.txt");
        String wordsFileContent = String.join("\n", WORDS);
        Files.writeString(wordsFile.toPath(), wordsFileContent);
        File wordsFile2 = new File(wordsDir, "words2.txt");
        Files.writeString(wordsFile2.toPath(), "newword");

        // Create the "content" cache file with the proper format.
        File dbDir = new File(tempDir, "db");
        assertTrue(dbDir.mkdirs());
        File contentsFile = new File(dbDir, "contents");
        // The contents cache is missing words2.txt, so the DB will be regenerated
        Files.writeString(contentsFile.toPath(), "words.txt,18");

        dictionary = new SpellDictionaryDisk(tempDir, null, true);
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
        Path baseDir = Files.createTempDirectory(TEST_DIR_PREFIX);
        assertThrows(FileNotFoundException.class, () ->
            new SpellDictionaryDisk(baseDir.toFile(), null, true)
        );
    }

    @Test
    void testSetup_dbSubdirExists() throws IOException {
        File baseDir = Files.createTempDirectory(TEST_DIR_PREFIX).toFile();
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
    void testGetWords_emptyString() {
        List<String> words = dictionary.getWords("");
        assertEquals(0, words.size());
    }

    @Test
    void testGetWords_happyPath() {
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

    @Test
    void testCodeWord_equals_sameObject() {
        SpellDictionaryDisk.CodeWord codeWord = new SpellDictionaryDisk.CodeWord("CODE", "word");
        assertTrue(codeWord.equals(codeWord));
    }

    @Test
    void testCodeWord_equals_differentWords_equal() {
        SpellDictionaryDisk.CodeWord codeWord = new SpellDictionaryDisk.CodeWord("CODE", "word");
        SpellDictionaryDisk.CodeWord codeWord2 = new SpellDictionaryDisk.CodeWord("CODE", "word");
        assertTrue(codeWord.equals(codeWord2));
    }

    @Test
    void testCodeWord_equals_differentWords_differentWords() {
        SpellDictionaryDisk.CodeWord codeWord = new SpellDictionaryDisk.CodeWord("CODE", "word");
        SpellDictionaryDisk.CodeWord codeWord2 = new SpellDictionaryDisk.CodeWord("CODE", "word2");
        assertFalse(codeWord.equals(codeWord2));
    }

    @Test
    void testCodeWord_equals_differentTypes() {
        SpellDictionaryDisk.CodeWord codeWord = new SpellDictionaryDisk.CodeWord("CODE", "word");
        assertFalse(codeWord.equals("word"));
    }

    @Test
    void testCodeWord_hashCode() {
        SpellDictionaryDisk.CodeWord codeWord = new SpellDictionaryDisk.CodeWord("CODE", "word");
        assertEquals("word".hashCode(), codeWord.hashCode());
    }

   @Test
   void testFileSize_equals_sameObject() {
       SpellDictionaryDisk.FileSize fileSize = new SpellDictionaryDisk.FileSize("file.txt", 100);
       assertTrue(fileSize.equals(fileSize));
   }

   @Test
   void testFileSize_equals_differentObjects_equal() {
       SpellDictionaryDisk.FileSize fileSize1 = new SpellDictionaryDisk.FileSize("file.txt", 100);
       SpellDictionaryDisk.FileSize fileSize2 = new SpellDictionaryDisk.FileSize("file.txt", 100);
       assertTrue(fileSize1.equals(fileSize2));
   }

   @Test
   void testFileSize_equals_differentObjects_notEqual_differentFileName() {
       SpellDictionaryDisk.FileSize fileSize1 = new SpellDictionaryDisk.FileSize("file.txt", 100);
       SpellDictionaryDisk.FileSize fileSize2 = new SpellDictionaryDisk.FileSize("file2.txt", 100);
       assertFalse(fileSize1.equals(fileSize2));
   }

    @Test
    void testFileSize_equals_differentObjects_notEqual_differentFileSize() {
        SpellDictionaryDisk.FileSize fileSize1 = new SpellDictionaryDisk.FileSize("file.txt", 100);
        SpellDictionaryDisk.FileSize fileSize2 = new SpellDictionaryDisk.FileSize("file.txt", 101);
        assertFalse(fileSize1.equals(fileSize2));
    }

   @Test
   void testFileSize_equals_differentTypes() {
       SpellDictionaryDisk.FileSize fileSize = new SpellDictionaryDisk.FileSize("file.txt", 100);
       assertFalse(fileSize.equals("file.txt"));
   }

   @Test
   void testFileSize_hashCode() {
       SpellDictionaryDisk.FileSize fileSize = new SpellDictionaryDisk.FileSize("file.txt", 100);
       assertNotEquals(0, fileSize.hashCode());
   }
}
