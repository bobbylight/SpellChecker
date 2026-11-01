package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.engine.SpellDictionary;
import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.com.swabunga.spell.event.SpellChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
    void testGet_rtl() {
        Locale origDefault = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINA);
            SpellingParserNotice notice = new SpellingParserNotice(parser, "msg", 0, 4, "mispelled", sc);
            String html = new SpellingErrorTooltipHtmlGenerator().get(sc, notice);
            Assertions.assertFalse(html.contains("dir=\"rtl\""));
        } finally {
            Locale.setDefault(origDefault);
        }
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

    private static String getSuggestionTable(String html) {
        int start = html.indexOf("<table");
        int end = html.indexOf("</table>") + "</table>".length();
        Assertions.assertTrue(start > -1 && end > start, "No suggestion table found");
        return html.substring(start, end);
    }
}
