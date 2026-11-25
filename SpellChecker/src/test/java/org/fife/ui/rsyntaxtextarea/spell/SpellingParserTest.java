package org.fife.ui.rsyntaxtextarea.spell;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;

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
        var dictionary = new SpellDictionaryHashMap();
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
            var entry = new ZipEntry("eng_com.dic");
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
    void testCreateEnglishSpellingParser_missingEntry() throws IOException {
        File zipFile = File.createTempFile("scUnitTests_spellingParser_missing_entry", ".zip");
        zipFile.deleteOnExit();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zos.putNextEntry(new ZipEntry("eng_com.dic"));
            zos.write("aardvark\n".getBytes());
            zos.closeEntry();
        }

        IOException e = assertThrows(IOException.class,
                () -> SpellingParser.createEnglishSpellingParser(zipFile, true, false));
        assertTrue(e.getMessage().contains("color.dic"), e.getMessage());
    }

    @Test
    void testCreateEnglishSpellingParser_readsEntriesAsUtf8() throws IOException, BadLocationException {
        File zipFile = File.createTempFile("scUnitTests_spellingParser_utf8", ".zip");
        zipFile.deleteOnExit();
        String[] entries = { "eng_com.dic", "color.dic", "labeled.dic", "center.dic", "ize.dic", "yze.dic" };
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String entry : entries) {
                zos.putNextEntry(new ZipEntry(entry));
                if ("eng_com.dic".equals(entry)) {
                    zos.write("caf\u00e9\n".getBytes(StandardCharsets.UTF_8));
                }
                zos.closeEntry();
            }
        }

        SpellingParser utf8Parser = SpellingParser.createEnglishSpellingParser(zipFile, true, false);
        var doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE);
        doc.insertString(0, "caf\u00e9", null);
        ParseResult result = utf8Parser.parse(doc, SyntaxConstants.SYNTAX_STYLE_NONE);
        assertTrue(result.getNotices().isEmpty(), () -> result.getNotices().toString());
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
        assertThrows(NullPointerException.class, () -> parser.setSpellCheckableTokenIdentifier(null));
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

    /**
     * The tooltip HTML refers to its images relative to the image base. The
     * test classpath has compiled classes and resources in separate folders,
     * as an IDE does, so the base must be the folder with the images, not just
     * the first folder for the package.
     */
    @ParameterizedTest
    @ValueSource(strings = { "add.png", "cross.png", "lightbulb.png", "spellcheck.png" })
    void testGetImageBase_resolvesTooltipImages(String image) throws IOException {
        URL url = URI.create(parser.getImageBase().toExternalForm() + image).toURL();
        try (InputStream in = url.openStream()) {
            assertTrue(in.read() != -1);
        }
    }

    @Test
    void testLinkClicked_listenersNotified_addWithUserDictionary_notification() throws IOException {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        File userDictFile = File.createTempFile("scUnitTests_spellingParser", ".txt");
        userDictFile.deleteOnExit();
        parser.setUserDictionary(userDictFile);

        String desc = "ADD://newword";
        var e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        // Proper event fired
        assertEquals(1, events.size());
        SpellingParserEvent event = events.getFirst();
        assertEquals(SpellingParserEvent.Type.WORD_ADDED, event.getType());
        assertEquals(parser, event.getParser());
        assertEquals(textArea, event.getTextArea());
        assertEquals("newword", event.getWord());

        // The user dictionary file is actually updated
        assertEquals("newword\n", Files.readString(userDictFile.toPath()));
    }

    @Test
    void testLinkClicked_listenersNotified_addWithoutUserDictionary_noNotification() throws BadLocationException {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        String desc = "ADD://newword";
        var e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        assertEquals(0, events.size());

        // The word isn't added to the in-memory user dictionary either
        var doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE);
        doc.insertString(0, "newword", null);
        assertEquals(1, parser.parse(doc, SyntaxConstants.SYNTAX_STYLE_NONE).getNotices().size());
    }

    @Test
    void testLinkClicked_listenersNotified_ignore() {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        String desc = "IGNORE://newword";
        var e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        assertEquals(1, events.size());
        SpellingParserEvent event = events.getFirst();
        assertEquals(SpellingParserEvent.Type.WORD_IGNORED, event.getType());
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

        String desc = "REPLACE://1,3,replacement,123";
        var e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc);
        parser.linkClicked(textArea, e);

        assertEquals(0, events.size());
        assertEquals("0replacement456789", textArea.getText());
    }

    @Test
    void testLinkClicked_replace_selectsReplacement() {
        textArea.setText("the cta sat");

        String desc = "REPLACE://4,3,cat,cta";
        parser.linkClicked(textArea, new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc));

        assertEquals("the cat sat", textArea.getText());
        assertEquals("cat", textArea.getSelectedText());
    }

    @Test
    void testLinkClicked_replace_wordNoLongerAtOffset_notReplaced() {
        // The link was made when "cta" was at offset 4, but the text has since changed
        textArea.setText("a the cta sat");

        String desc = "REPLACE://4,3,cat,cta";
        parser.linkClicked(textArea, new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc));

        assertEquals("a the cta sat", textArea.getText());
    }

    @Test
    void testLinkClicked_replace_offsetPastEndOfDocument_notReplaced() {
        textArea.setText("the c");

        String desc = "REPLACE://4,3,cat,cta";
        assertDoesNotThrow(() -> parser.linkClicked(textArea,
            new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, desc)));

        assertEquals("the c", textArea.getText());
    }

    /**
     * Code review bug 1: after an edit, the notices keep their old offsets
     * until the next reparse (1.25 seconds after the last edit by default).
     * A tool tip shown in that window, here over "cat", which now sits where
     * "sta" used to be, offered to replace "sta" at its old offset.
     */
    @Test
    void testLinkClicked_replace_toolTipShownBeforeReparse_doesNotReplaceWrongText() throws Exception {
        var dictionary = new SpellDictionaryHashMap();
        for (String word : new String[] { "a", "this", "cat", "sat" }) {
            dictionary.addWord(word);
        }
        parser = new SpellingParser(dictionary);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        textArea.setUseFocusableTips(false); // So getToolTipText() returns the tip's HTML
        textArea.setSize(800, 200);
        textArea.setText("this cat sta");
        textArea.addParser(parser);
        textArea.forceReparsing(parser); // "sta" is misspelled, at 9-12

        textArea.insert("a ", 0); // Before the next reparse: "a this cat sta"

        // Hover over offset 9, where "sta" was and the "t" of "cat" now is
        Rectangle r = textArea.modelToView2D(9).getBounds();
        var hover = new MouseEvent(textArea, MouseEvent.MOUSE_MOVED, 0, 0,
            r.x + 1, r.y + r.height / 2, 0, false);
        String tip = textArea.getToolTipText(hover);
        assertNotNull(tip);
        Matcher m = Pattern.compile("REPLACE://[^\"]+").matcher(tip);
        assertTrue(m.find(), tip);

        parser.linkClicked(textArea, new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ACTIVATED, null, m.group()));

        assertEquals("a this cat sta", textArea.getText());
    }

    @Test
    void testLinkClicked_listenersNotified_linkEntered_noNotification() {
        List<SpellingParserEvent> events = new ArrayList<>();
        SpellingParserListener listener = events::add;
        parser.addSpellingParserListener(listener);

        // The mouse entering a hyperlink doesn't do anything
        String desc = "ADD://newword";
        var e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.ENTERED, null, desc);
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
        var e = new HyperlinkEvent(textArea, HyperlinkEvent.EventType.EXITED, null, desc);
        parser.linkClicked(textArea, e);

        // No events fired
        assertEquals(0, events.size());
    }

    @Test
    void testParse_plainText_hyphenatedWords() throws BadLocationException {
        var doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE);
        doc.insertString(0, "This is a bat-cave test-caev.", null);
        ParseResult result = parser.parse(doc, SyntaxConstants.SYNTAX_STYLE_NONE);

        // Only the misspelled part of a hyphenated word is flagged
        List<ParserNotice> notices = result.getNotices();
        assertEquals(1, notices.size());
        ParserNotice notice = notices.getFirst();
        assertEquals(24, notice.getOffset());
        assertEquals(4, notice.getLength());
        assertEquals("Incorrectly spelled word: caev", notice.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = SyntaxConstants.SYNTAX_STYLE_NONE)
    void testParse_plainText(String syntaxStyle) throws BadLocationException {
        var doc = new RSyntaxDocument(syntaxStyle);
        doc.insertString(0, "Thiss is a test.", null);
        ParseResult result = parser.parse(doc, null);

        // Correct notices
        List<ParserNotice> notices = result.getNotices();
        assertEquals(1, notices.size());
        ParserNotice notice = notices.getFirst();
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
        var doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_JAVA);
        doc.insertString(0, "// Thiss is a test.\nThiss error will not be flagged", null);
        ParseResult result = parser.parse(doc, SyntaxConstants.SYNTAX_STYLE_JAVA);

        // Correct notices
        List<ParserNotice> notices = result.getNotices();
        assertEquals(1, notices.size());
        ParserNotice notice = notices.getFirst();
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
        var doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_JAVA);
        doc.insertString(0, "// Thiss is a test. Thiss is a test", null);
        parser.setMaxErrorCount(1);
        ParseResult result = parser.parse(doc, SyntaxConstants.SYNTAX_STYLE_JAVA);

        // Only the first spelling error is found, since the check is canceled at 1
        List<ParserNotice> notices = result.getNotices();
        assertEquals(1, notices.size());
        ParserNotice notice = notices.getFirst();
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
