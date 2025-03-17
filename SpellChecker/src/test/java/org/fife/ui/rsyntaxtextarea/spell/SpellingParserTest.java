package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.spell.event.SpellingParserEvent;
import org.fife.ui.rsyntaxtextarea.spell.event.SpellingParserListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SpellingParser}.
 */
class SpellingParserTest {

    private SpellingParser parser;
    private RSyntaxTextArea textArea;

    private static final String[] WORDS = { "a", "aardvark", "apple", "bat", "cave", "is", "test", "this" };

    @BeforeEach
    void setUp() throws IOException {
        SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap();
        for (String word : WORDS) {
            dictionary.addWord(word);
        }
        parser = new SpellingParser(dictionary);
        textArea = new RSyntaxTextArea();
    }

    private static File createZipFile(boolean american, boolean programming) throws IOException {
        File zipFile = File.createTempFile("scUnitTests_spellingParser_eng_dictionary", ".zip");
        zipFile.deleteOnExit();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry entry = new ZipEntry("eng_com.dic");
            zos.putNextEntry(entry);
            zos.write("aardvark\nappla\nbat\n".getBytes());
            zos.closeEntry();

            // US English entries
            entry = new ZipEntry("color.dic");
            zos.putNextEntry(entry);
            zos.write("color\n".getBytes());
            entry = new ZipEntry("labeled.dic");
            zos.putNextEntry(entry);
            zos.write("labeled\n".getBytes());
            entry = new ZipEntry("center.dic");
            zos.putNextEntry(entry);
            zos.write("center\n".getBytes());
            entry = new ZipEntry("ize.dic");
            zos.putNextEntry(entry);
            zos.write("sanitize\n".getBytes());
            entry = new ZipEntry("yze.dic");
            zos.putNextEntry(entry);
            zos.write("analyze\n".getBytes());

            // UK English entries
            entry = new ZipEntry("colour.dic");
            zos.putNextEntry(entry);
            zos.write("colour\n".getBytes());
            entry = new ZipEntry("labelled.dic");
            zos.putNextEntry(entry);
            zos.write("labelled\n".getBytes());
            entry = new ZipEntry("centre.dic");
            zos.putNextEntry(entry);
            zos.write("centre\n".getBytes());
            entry = new ZipEntry("ise.dic");
            zos.putNextEntry(entry);
            zos.write("sanitise\n".getBytes());
            entry = new ZipEntry("yse.dic");
            zos.putNextEntry(entry);
            zos.write("analyse\n".getBytes());

            // Programming entries
            entry = new ZipEntry("programming.dic");
            zos.putNextEntry(entry);
            zos.write("gradle\n".getBytes());
        }
        return zipFile;
    }

    @Test
    void testAddSpellingParserListener() {
        SpellingParserListener listener = e -> {};
        assertDoesNotThrow(() -> parser.addSpellingParserListener(listener));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testCreateEnglishSpellingParser_2Arg(boolean american) throws IOException {
        File zipFile = createZipFile(american, false);
        SpellingParser englishParser = SpellingParser.createEnglishSpellingParser(zipFile, american);
        assertNotNull(englishParser);
    }

    @ParameterizedTest
    @CsvSource({ "false,false", "false,true", "true,false", "true,true" })
    void testCreateEnglishSpellingParser_3Arg(boolean american, boolean programming) throws IOException {
        File zipFile = createZipFile(american, programming);
        SpellingParser englishParser = SpellingParser.createEnglishSpellingParser(zipFile, american, programming);
        assertNotNull(englishParser);
    }

    @Test
    void testGetSetAllowAdd() {
        assertTrue(parser.getAllowAdd());
        parser.setAllowAdd(false);
        assertFalse(parser.getAllowAdd());
    }

    @Test
    void testGetSetAllowIgnore() {
        assertTrue(parser.getAllowIgnore());
        parser.setAllowIgnore(false);
        assertFalse(parser.getAllowIgnore());
    }

    @Test
    void testGetSetMaxErrorCount() {
        assertEquals(100, parser.getMaxErrorCount());
        parser.setMaxErrorCount(50);
        assertEquals(50, parser.getMaxErrorCount());
    }

    @Test
    void testGetSetSpellCheckableTokenIdentifier() {
        assertNotNull(parser.getSpellCheckableTokenIdentifier());
        SpellCheckableTokenIdentifier identifier = new DefaultSpellCheckableTokenIdentifier();
        parser.setSpellCheckableTokenIdentifier(identifier);
        assertEquals(identifier, parser.getSpellCheckableTokenIdentifier());
    }

    @Test
    void testGetSetSpellCheckableTokenIdentifier_nullDisallowed() {
        assertThrows(IllegalArgumentException.class, () -> parser.setSpellCheckableTokenIdentifier(null));
    }

    @Test
    void testGetSetSquiggleUnderlineColor() {
        assertEquals(Color.BLUE, parser.getSquiggleUnderlineColor());
        parser.setSquiggleUnderlineColor(Color.RED);
        assertEquals(Color.RED, parser.getSquiggleUnderlineColor());
    }

    @Test
    void testGetSetUserDictionary_existingFile() throws IOException {
        assertNull(parser.getUserDictionary());

        File userDictFile = File.createTempFile("scUnitTests_spellingParser", ".txt");
        userDictFile.deleteOnExit();
        Files.writeString(userDictFile.toPath(), "aardvark\napple\nbat\ncave");
        parser.setUserDictionary(userDictFile);
        assertEquals(userDictFile, parser.getUserDictionary());
    }

    @Test
    void testGetSetUserDictionary_fileDoesNotExist() throws IOException {
        assertNull(parser.getUserDictionary());

        File userDictFile = File.createTempFile("scUnitTests_spellingParser", ".txt");
        assertTrue(userDictFile.delete());
        parser.setUserDictionary(userDictFile);
        assertEquals(userDictFile, parser.getUserDictionary());
        assertTrue(userDictFile.exists()); // Gets created if it doesn't initially exist
    }

    @Test
    void testGetSetUserDictionary_null() {
        assertNull(parser.getUserDictionary());
        assertDoesNotThrow(() -> parser.setUserDictionary(null));
        assertNull(parser.getUserDictionary());
    }

    @Test
    void testGetSetUserDictionary_throwsIfCannotBeCreated() throws IOException{
        assertNull(parser.getUserDictionary());
        File mockDictionaryFile = Mockito.mock(File.class);
        Mockito.doReturn(false).when(mockDictionaryFile).exists();
        Mockito.doReturn(false).when(mockDictionaryFile).createNewFile();
        assertThrows(IOException.class, () -> parser.setUserDictionary(mockDictionaryFile));
    }

    @Test
    void testGetImageBase() {
        assertNotNull(parser.getImageBase());
    }

    @Test
    void testLinkClicked_listenersNotified_addWithoutUserDictionary_noNotification() throws IOException {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        File userDictFile = File.createTempFile("scUnitTests_spellingParser", ".txt");
        userDictFile.deleteOnExit();
        parser.setUserDictionary(userDictFile);

        String desc = "ADD://newword";
        HyperlinkEvent e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        // Proper event fired
        assertEquals(1, events.size());
        SpellingParserEvent event = events.get(0);
        assertEquals(SpellingParserEvent.WORD_ADDED, event.getType());
        assertEquals(parser, event.getParser());
        assertEquals(textArea, event.getTextArea());
        assertEquals("newword", event.getWord());

        // The user dictionary file is actually updated
        assertEquals("newword\n", Files.readString(userDictFile.toPath()));
    }

    @Test
    void testLinkClicked_listenersNotified_addWithUserDictionary_notification() {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        String desc = "ADD://newword";
        HyperlinkEvent e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        assertEquals(0, events.size());
    }

    @Test
    void testLinkClicked_listenersNotified_ignore() {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        String desc = "IGNORE://newword";
        HyperlinkEvent e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        assertEquals(1, events.size());
        SpellingParserEvent event = events.get(0);
        assertEquals(SpellingParserEvent.WORD_IGNORED, event.getType());
        assertEquals(parser, event.getParser());
        assertEquals(textArea, event.getTextArea());
        assertEquals("newword", event.getWord());
    }

    @Test
    void testLinkClicked_listenersNotified_replace_noNotification() {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);
        textArea.setText("0123456789");

        String desc = "REPLACE://1,3,replacement";
        HyperlinkEvent e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        assertEquals(0, events.size());
        assertEquals("0replacement456789", textArea.getText());
    }

    @Test
    void testLinkClicked_listenersNotified_linkEntered_noNotification() {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        // The mouse entering a hyperlink doesn't do anything
        String desc = "ADD://newword";
        HyperlinkEvent e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ENTERED, null, desc);
        parser.linkClicked(textArea, e);

        // No events fired
        assertEquals(0, events.size());
    }

    @Test
    void testLinkClicked_listenersNotified_linkExited_noNotification() {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        // The mouse exiting a hyperlink doesn't do anything
        String desc = "ADD://newword";
        HyperlinkEvent e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.EXITED, null, desc);
        parser.linkClicked(textArea, e);

        // No events fired
        assertEquals(0, events.size());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = SyntaxConstants.SYNTAX_STYLE_NONE)
    void testParse_plainText(String syntaxStyle) throws BadLocationException {
        RSyntaxDocument doc = new RSyntaxDocument(syntaxStyle);
        doc.insertString(0, "Thiss is a test.", null);
        ParseResult result = parser.parse(doc, null);

        // Correct notices
        List<ParserNotice> notices = result.getNotices();
        assertEquals(1, notices.size());
        ParserNotice notice = notices.get(0);
        assertEquals(0, notice.getOffset());
        assertEquals(5, notice.getLength());
        assertTrue(notice.getKnowsOffsetAndLength());
        assertEquals("Incorrectly spelled word: Thiss", notice.getMessage());
        assertEquals(ParserNotice.Level.INFO, notice.getLevel());
        assertEquals(parser, notice.getParser());
        assertEquals(Color.BLUE, notice.getColor());
        assertNotNull(notice.getToolTipText());
        assertEquals("[SpellingParserNotice: Thiss]", notice.toString());

        // Correct other fields
        assertEquals(parser, result.getParser());
        assertNull(result.getError());
        assertEquals(0, result.getFirstLineParsed());
        assertEquals(doc.getDefaultRootElement().getElementCount() - 1, result.getLastLineParsed());
    }

    @Test
    void testParse_nonPlainText() throws BadLocationException {
        RSyntaxDocument doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_JAVA);
        doc.insertString(0, "// Thiss is a test.\nThiss error will not be flagged", null);
        ParseResult result = parser.parse(doc, SyntaxConstants.SYNTAX_STYLE_JAVA);

        // Correct notices
        List<ParserNotice> notices = result.getNotices();
        assertEquals(1, notices.size());
        ParserNotice notice = notices.get(0);
        assertEquals(3, notice.getOffset());
        assertEquals(5, notice.getLength());
        assertTrue(notice.getKnowsOffsetAndLength());
        assertEquals("Incorrectly spelled word: Thiss", notice.getMessage());
        assertEquals(ParserNotice.Level.INFO, notice.getLevel());
        assertEquals(parser, notice.getParser());
        assertEquals(Color.BLUE, notice.getColor());
        assertNotNull(notice.getToolTipText());
        assertEquals("[SpellingParserNotice: Thiss]", notice.toString());

        // Correct other fields
        assertEquals(parser, result.getParser());
        assertNull(result.getError());
        assertEquals(0, result.getFirstLineParsed());
        assertEquals(doc.getDefaultRootElement().getElementCount() - 1, result.getLastLineParsed());
    }

    @Test
    void testParse_nonPlainText_stopsAtMaxSpellingErrors() throws BadLocationException {
        RSyntaxDocument doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_JAVA);
        doc.insertString(0, "// Thiss is a test. Thiss is a test", null);
        parser.setMaxErrorCount(1);
        ParseResult result = parser.parse(doc, SyntaxConstants.SYNTAX_STYLE_JAVA);

        // Only the first spelling error is found, since the check is canceled at 1
        List<ParserNotice> notices = result.getNotices();
        assertEquals(1, notices.size());
        ParserNotice notice = notices.get(0);
        assertEquals(3, notice.getOffset());
        assertEquals(5, notice.getLength());
        assertTrue(notice.getKnowsOffsetAndLength());
        assertEquals("Incorrectly spelled word: Thiss", notice.getMessage());
        assertEquals(ParserNotice.Level.INFO, notice.getLevel());
        assertEquals(parser, notice.getParser());
        assertEquals(Color.BLUE, notice.getColor());
        assertNotNull(notice.getToolTipText());
        assertEquals("[SpellingParserNotice: Thiss]", notice.toString());

        // Correct other fields
        assertEquals(parser, result.getParser());
        assertNull(result.getError());
        assertEquals(0, result.getFirstLineParsed());
        assertEquals(doc.getDefaultRootElement().getElementCount() - 1, result.getLastLineParsed());
    }

    @Test
    void testRemoveSpellingParserListener() {
        SpellingParserListener listener = event -> {};
        parser.addSpellingParserListener(listener);
        assertDoesNotThrow(() -> parser.removeSpellingParserListener(listener));
    }
}
