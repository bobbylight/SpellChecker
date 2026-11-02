/*
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.engine.Word;
import org.fife.com.swabunga.spell.event.SpellChecker;

import java.awt.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Generates the HTML for a tooltip that displays a spelling error and its possible corrections.
 */
class SpellingErrorTooltipHtmlGenerator {

    // Use double quotes for attribute values; this is a MessageFormat pattern,
    // so single quotes would be treated as escapes and dropped (along with
    // any placeholder they surround).
    private static final String TOOLTIP_TEXT_FORMAT =
            "<html><body dir=\"{0}\">" +
                    "<img src=\"lightbulb.png\" width=\"16\" height=\"16\">{1}<hr>" +
                    "<img src=\"spellcheck.png\" width=\"16\" height=\"16\">{2}<br>{3}<br>&nbsp;";

    private static final ResourceBundle MSG = ResourceBundle.getBundle(
            "org.fife.ui.rsyntaxtextarea.spell.SpellingParser");

    public String get(SpellChecker sc, SpellingParserNotice notice) {
        StringBuilder sb = new StringBuilder();
        String spacing = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        String word = notice.getWord();

        List<Word> suggestions = sc.getSuggestions(word);
        if (suggestions.isEmpty()) {
            sb.append(spacing).append("&#8226;&nbsp;<em>");
            sb.append(MSG.getString("None"));
            sb.append("</em><br><br>");
        }
        else {

            // If the bad word started with an upper-case letter, make sure all our suggestions do.
            boolean capitalize = Character.isUpperCase(word.charAt(0));

            sb.append("<center>");
            sb.append("<table width='75%'>");
            for (int i = 0; i < suggestions.size(); i++) {
                if ((i % 2) == 0) {
                    sb.append("<tr>");
                }
                sb.append("<td>&#8226;&nbsp;");
                String suggestion = suggestions.get(i).getWord();
                if (capitalize) {
                    suggestion = Character.toUpperCase(suggestion.charAt(0)) + suggestion.substring(1);
                }
                // Surround with double quotes, not single, since
                // replacement words can have single quotes in them.
                sb.append("<a href=\"").append(SpellingErrorAction.REPLACE).append("://").
                        append(notice.getOffset()).append(',').
                        append(notice.getLength()).append(',').
                        append(suggestion).
                        append("\">").
                        append(suggestion).
                        append("</a>").
                        append("</td>");
                if ((i & 1) == 1) {
                    sb.append("</tr>");
                }
            }
            // An odd suggestion count leaves the last row half-full and unclosed
            if ((suggestions.size() & 1) == 1) {
                sb.append("<td></td></tr>");
            }
            sb.append("</table>");
            sb.append("</center>");
        }

        // As above, the ADD and IGNORE links' hrefs are double-quoted since the
        // misspelled word can contain single quotes (e.g. "would'nt").
        SpellingParser sp = (SpellingParser)notice.getParser();
        if (sp.getAllowAdd()) {
            sb.append("<img src='add.png' width='16' height='16'>&nbsp;").
                    append("<a href=\"").append(SpellingErrorAction.ADD).
                    append("://").append(word).append("\">").
                    append(MSG.getString("ErrorToolTip.AddToDictionary")).
                    append("</a><br>");
        }

        if (sp.getAllowIgnore()) {
            String text = MSG.getString("ErrorToolTip.IgnoreWord");
            text = MessageFormat.format(text, word);
            sb.append("<img src='cross.png' width='16' height='16'>&nbsp;").
                    append("<a href=\"").append(SpellingErrorAction.IGNORE).
                    append("://").append(word).append("\">").
                    append(text).append("</a>");
        }

        String firstLine = MessageFormat.format(
                MSG.getString("ErrorToolTip.DescHtml"),
                word);
        ComponentOrientation o = ComponentOrientation.getOrientation(
                Locale.getDefault());
        String dirAttr = o.isLeftToRight() ? "ltr" : "rtl";

        return MessageFormat.format(TOOLTIP_TEXT_FORMAT,
                dirAttr,
                firstLine,
                MSG.getString("ErrorToolTip.SuggestionsHtml"),
                sb.toString());
    }
}
