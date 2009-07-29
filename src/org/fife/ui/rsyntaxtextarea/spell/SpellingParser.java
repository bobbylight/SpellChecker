/*
 * 07/21/2009
 *
 * SpellingParser.java - A spell-checker for RSyntaxTextArea.
 * Copyright (C) 2009 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipFile;

import javax.swing.text.Element;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;


import com.swabunga.spell.engine.Configuration;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.DocumentWordTokenizer;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;


/**
 * A parser that spell-checks documents.  The spelling engine is a lightly
 * modified version of <a href="http://jazzy.sourceforge.net/">Jazzy</a>.
 * All Jazzy source, modified or otherwise, is licensed under the GPL.
 * This specific class, and any other RSyntaxTextArea-specific stuff, is
 * LGPL.<p>
 *
 * For source code only comments are spell checked.  For plain text files,
 * the entire content is spell checked.<p>
 *
 * Usage:
 * <pre>
 * RSyntaxTextArea textArea = new RSyntaxTextArea(40, 25);
 * SpellingDictionary dict = new SpellDictionaryHashMap(new File("eng_com.dic"));
 * SpellingParser parser = new SpellingParser(dict);
 * textArea.setParser(parser);
 * </pre>
 *
 * @author Robert Futrell
 * @version 0.1
 */
public class SpellingParser implements Parser, SpellCheckListener {

	private DefaultParseResult result;
	private SpellChecker sc;
	private RSyntaxDocument doc;
	private int startOffs;
	private Color squiggleUnderlineColor;
	private String noticePrefix;
	private String noticeSuffix;

	private static final String MSG = "org.fife.ui.rsyntaxtextarea.spell.SpellingParser";
	private static final ResourceBundle msg = ResourceBundle.getBundle(MSG);


	/**
	 * Constructor.
	 *
	 * @param dict The dictionary to use.
	 */
	public SpellingParser(SpellDictionary dict) {

		result = new DefaultParseResult(this);
		sc = new SpellChecker(dict);
		sc.addSpellCheckListener(this);
		setSquiggleUnderlineColor(Color.BLUE);

		// Since the spelling callback can possibly be called many times
		// per parsing, we're extremely cheap here and pre-split our message
		// format instead of using MessageFormat.
		String temp = msg.getString("IncorrectSpelling");
		int offs = temp.indexOf("{0}");
		noticePrefix = temp.substring(0, offs);
		noticeSuffix = temp.substring(offs+3);

	}


	/**
	 * A utility method to easily create a parser for American or British
	 * English.
	 *
	 * @param zip The location of the <code>english_dic.zip</code> file
	 *        distributed with the spell checker add-on.
	 * @param american Whether the parser should be for American (as opposed
	 *        to British) English.
	 * @return The parser.
	 * @throws IOException If an error occurs reading the zip file.
	 */
	public static SpellingParser createEnglishSpellingParser(File zip,
							boolean american) throws IOException {

		long start = System.currentTimeMillis();

		SpellDictionaryHashMap dict = null;

		ZipFile zf = new ZipFile(zip);

		try {

			// Words common to American and British English
			InputStream in = zf.getInputStream(zf.getEntry("eng_com.dic"));
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			try {
				dict = new SpellDictionaryHashMap(r);
			} finally {
				r.close();
			}

			String[] others = null;
			if (american) {
				others = new String[] { "color", "labeled", "center", "ize",
										"yze" };
			}
			else { // British
				others = new String[] { "colour", "labelled", "centre",
										"ise", "yse" };
			}

			// Load words specific to the English dialect.
			for (int i=0; i<others.length; i++) {
				in = zf.getInputStream(zf.getEntry(others[i] + ".dic"));
				r = new BufferedReader(new InputStreamReader(in));
				try {
					dict.addDictionary(r);
				} finally {
					r.close();
				}
			}

		} finally {
			zf.close();
		}

		float secs = (System.currentTimeMillis() - start)/1000f;
		System.out.println("Loading dictionary took " + secs + " seconds");

		return new SpellingParser(dict);

	}


	private final int getLineOfOffset(int offs) {
		return doc.getDefaultRootElement().getElementIndex(offs);
	}


	/**
	 * Returns the color to use when painting spelling errors in an editor.
	 *
	 * @return The color to use.
	 * @see #setSquiggleUnderlineColor(Color)
	 */
	public Color getSquiggleUnderlineColor() {
		return squiggleUnderlineColor;
	}


	/**
	 * {@inheritDoc}
	 */
private int count;
private boolean firstTime;
	public ParseResult parse(RSyntaxDocument doc, String style) {

		long startTime = System.currentTimeMillis();
		result.clearNotices();
		sc.reset();
		this.doc = doc;
count = 0;
firstTime = true;
		// Use a faster method for spell-checking plain text.
		if (style==null || SyntaxConstants.SYNTAX_STYLE_NONE.equals(style)) {
			parseEntireDocument(doc);
		}

		else {

			Element root = doc.getDefaultRootElement();
			int lineCount = root.getElementCount();

			for (int line=0; line<lineCount; line++) {

				Token t = doc.getTokenListForLine(line);
				while (t!=null && t.isPaintable()) {
					if (t.type==Token.COMMENT_EOL ||
							t.type==Token.COMMENT_MULTILINE ||
							t.type==Token.COMMENT_DOCUMENTATION) {
						startOffs = t.offset;
						// TODO: Create a wordTokenizer that uses char[] array
						// to prevent String allocation.
						StringWordTokenizer swt =
									new StringWordTokenizer(t.getLexeme());
						sc.checkSpelling(swt);
					}
					t = t.getNextToken();
				}

if (count==9 && firstTime) {
	firstTime = false;
	float secs = (System.currentTimeMillis() - startTime)/1000f;
	System.out.println("count==9 reached in: " + secs + " seconds");
}

			}

		}

		float secs = (System.currentTimeMillis() - startTime)/1000f;
		System.out.println("Spell check completed in: " + secs + " seconds");
System.out.println("... count==" + count);
		return result;

	}


	/**
	 * Spell-checks a plain text document.
	 *
	 * @param doc The document to spell check.
	 */
	private void parseEntireDocument(RSyntaxDocument doc) {
		DocumentWordTokenizer dwt = new DocumentWordTokenizer(doc);
		sc.checkSpelling(dwt);
	}


	/**
	 * Sets the color to use when painting spelling errors in an editor.
	 *
	 * @param color The color to use.
	 * @see #getSquiggleUnderlineColor()
	 */
	public void setSquiggleUnderlineColor(Color color) {
		squiggleUnderlineColor = color;
	}


	/**
	 * Callback called when a spelling error is found.
	 *
	 * @param e The event.
	 */
	public void spellingError(SpellCheckEvent e) {
count++;
//		e.ignoreWord(true);
		String word = e.getInvalidWord();
		int offs = startOffs + e.getWordContextPosition();
		int line = getLineOfOffset(offs);
		String text = noticePrefix + word + noticeSuffix;
		SpellingParserNotice notice =
			new SpellingParserNotice(this, text, line, offs, word, sc);
		notice.setColor(getSquiggleUnderlineColor());
		result.addNotice(notice);
	}


	/**
	 * The notice type returned by this parser.
	 */
	private static class SpellingParserNotice extends ParserNotice {

		private String word;
		private SpellChecker sc;

		public SpellingParserNotice(SpellingParser parser, String msg,
									int line, int offs, String word,
									SpellChecker sc) {
			super(parser, msg, line, offs, word.length());
			this.word = word;
			this.sc = sc;
		}

		public String getToolTipText() {

			String temp = msg.getString("SpellingErrorHtml");

			StringBuffer sb = new StringBuffer();
			String spacing = "&nbsp;&nbsp;&nbsp;";
			int threshold = sc.getConfiguration().getInteger(Configuration.SPELL_THRESHOLD);
			List suggestions = sc.getSuggestions(word, threshold);
			if (suggestions==null || suggestions.size()==0) {
				sb.append(spacing).append("<em>None</em>");
			}
			else {
				for (Iterator i=suggestions.iterator(); i.hasNext(); ) {
					Word suggestion = (Word)i.next();
					sb.append(spacing).
							append(suggestion.getWord()).append("<br>");
				}
			}

			temp = MessageFormat.format(temp,
							new String[] { word, sb.toString() });
			return temp;

		}

	}


}