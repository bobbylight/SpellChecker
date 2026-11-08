package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.engine.SpellDictionary;
import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.event.SpellChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

/**
 * Unit tests for {@link SpellingErrorTooltipHtmlGenerator}.
 */
class SpellingErrorTooltipHtmlGeneratorTest {

    private SpellDictionary dictionary;
    private SpellChecker sc;
    private SpellingParser parser;

    @BeforeEach
    void setUp() throws IOException {
        dictionary = new SpellDictionaryHashMap();
        sc = new SpellChecker(dictionary);
        parser = new SpellingParser(dictionary);
    }

    @Test
    void testGet_evenSuggestionCount() {
        dictionary.addWord("misspelled");
        dictionary.addWord("dispelled");
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "mispelled", sc);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        Assertions.assertTrue(html.contains("Incorrectly spelled word:"));
        Assertions.assertTrue(html.contains("mispelled"));
        Assertions.assertTrue(html.contains("Suggestions:"));
        Assertions.assertTrue(html.contains("misspelled"));
        Assertions.assertTrue(html.contains("dispelled"));
        Assertions.assertTrue(html.contains("Add to dictionary"));
        Assertions.assertTrue(html.contains("Ignore 'mispelled' for this session"));

        // Two suggestions fill a single row exactly; no padding cell is needed
        String table = getSuggestionTable(html);
        Assertions.assertEquals(1, countOccurrences(table, "<tr>"));
        Assertions.assertEquals(1, countOccurrences(table, "</tr>"));
        Assertions.assertFalse(table.contains("<td></td>"));
    }

    @Test
    void testGet_capitalizedWord_suggestionsCapitalized() {
        dictionary.addWord("misspelled");
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "Mispelled", sc);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        // Both the replacement in the link and the displayed text are capitalized
        Assertions.assertTrue(html.contains("<a href=\"REPLACE://4,9,Misspelled\">Misspelled</a>"));
        Assertions.assertFalse(html.contains("misspelled"));
    }

    @Test
    void testGet_noSuggestions() {
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "mispelled", sc);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        Assertions.assertTrue(html.contains("Incorrectly spelled word:"));
        Assertions.assertTrue(html.contains("mispelled"));
        Assertions.assertTrue(html.contains("Suggestions:"));
        Assertions.assertTrue(html.contains("None"));
        Assertions.assertTrue(html.contains("Add to dictionary"));
        Assertions.assertTrue(html.contains("Ignore 'mispelled' for this session"));
    }

    @Test
    void testGet_oddSuggestionCount() {
        dictionary.addWord("misspelled");
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "mispelled", sc);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        Assertions.assertTrue(html.contains("Incorrectly spelled word:"));
        Assertions.assertTrue(html.contains("mispelled"));
        Assertions.assertTrue(html.contains("Suggestions:"));
        Assertions.assertTrue(html.contains("misspelled"));
        Assertions.assertTrue(html.contains("Add to dictionary"));
        Assertions.assertTrue(html.contains("Ignore 'mispelled' for this session"));

        // A single suggestion leaves the row half-full, so it's padded and closed
        String table = getSuggestionTable(html);
        Assertions.assertEquals(1, countOccurrences(table, "<tr>"));
        Assertions.assertEquals(1, countOccurrences(table, "</tr>"));
        Assertions.assertTrue(table.endsWith("<td></td></tr></table>"));
    }

    @Test
    void testGet_options_disallowAdd() {
        dictionary.addWord("misspelled");
        dictionary.addWord("dispelled");
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "mispelled", sc);
        parser.setAllowAdd(false);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        Assertions.assertFalse(html.contains("Add to dictionary"));
    }

    @Test
    void testGet_options_disallowIgnore() {
        dictionary.addWord("misspelled");
        dictionary.addWord("dispelled");
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "mispelled", sc);
        parser.setAllowIgnore(false);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        Assertions.assertFalse(html.contains("Ignore 'mispelled' for this session"));
    }

    @Test
    void testGet_wordWithApostrophe_addLinkContainsWholeWord() {
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 8, "would'nt", sc);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        Assertions.assertEquals("ADD://would'nt", getHref(html, "ADD://"));
    }

    @Test
    void testGet_wordWithApostrophe_ignoreLinkContainsWholeWord() {
        SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 8, "would'nt", sc);

        String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);

        Assertions.assertEquals("IGNORE://would'nt", getHref(html, "IGNORE://"));
    }

    @Test
    void testGet_ltr() {
        String html = getHtmlWithDefaultLocale(Locale.US);
        Assertions.assertTrue(html.contains("<body dir=\"ltr\">"));
    }

    @Test
    void testGet_rtl() {
        // Hebrew is used rather than e.g. Arabic because we have no Hebrew
        // translation. The generator caches its ResourceBundle statically, so
        // a translated locale here could leak into other tests' assertions.
        String html = getHtmlWithDefaultLocale(Locale.forLanguageTag("he"));
        Assertions.assertTrue(html.contains("<body dir=\"rtl\">"));
    }

    private static int countOccurrences(String text, String substring) {
        int count = 0;
        int offs = text.indexOf(substring);
        while (offs > -1) {
            count++;
            offs = text.indexOf(substring, offs + substring.length());
        }
        return count;
    }

    /**
     * Returns the {@code href} of the first link whose {@code href} starts with
     * the given prefix, as Swing's HTML parser (which renders the tooltip) sees it.
     */
    private static String getHref(String html, String prefix) {
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
        try {
            kit.read(new StringReader(html), doc, 0);
        } catch (IOException | BadLocationException e) {
            throw new AssertionError(e);
        }
        for (HTMLDocument.Iterator it = doc.getIterator(HTML.Tag.A); it.isValid(); it.next()) {
            Object href = it.getAttributes().getAttribute(HTML.Attribute.HREF);
            if (href != null && href.toString().startsWith(prefix)) {
                return href.toString();
            }
        }
        return Assertions.fail("No link found with href starting with: " + prefix);
    }

    private String getHtmlWithDefaultLocale(Locale locale) {
        Locale origDefault = Locale.getDefault();
        try {
            Locale.setDefault(locale);
            SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "mispelled", sc);
            return new SpellingErrorTooltipHtmlGenerator().get(sc, notice);
        } finally {
            Locale.setDefault(origDefault);
        }
    }

    private static String getSuggestionTable(String html) {
        int start = html.indexOf("<table");
        int end = html.indexOf("</table>") + "</table>".length();
        Assertions.assertTrue(start > -1 && end > start, "No suggestion table found");
        return html.substring(start, end);
    }
}
