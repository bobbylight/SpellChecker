/*
 * 08/29/2009
 *
 * SpellingParserEvent.java - An event fired by the spelling parser.
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
package org.fife.ui.rsyntaxtextarea.spell.event;

import java.util.EventObject;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;


/**
 * An event fired by the spelling parser.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SpellingParserEvent extends EventObject {

	/**
	 * Event type specifying that a word was added to the user's dictionary.
	 */
	public static final int WORD_ADDED		= 0;

	/**
	 * Event type specifying that a word will be ignored for the rest of
	 * this JVM session.
	 */
	public static final int WORD_IGNORED	= 1;

	private RSyntaxTextArea textArea;
	private int type;
	private String word;


	/**
	 * Constructor.
	 *
	 * @param source The source parser.
	 * @param textArea The text area that was parsed.
	 * @param type The type of event.
	 * @param word The word being added or ignored.
	 * @throws IllegalArgumentException If <code>type</code> is invalid.
	 */
	public SpellingParserEvent(SpellingParser source, RSyntaxTextArea textArea,
								int type, String word) {
		super(source);
		this.textArea = textArea;
		setType(type);
		this.word = word;
	}


	/**
	 * Returns the parser that fired this event.  This is a wrapper for
	 * <code>(SpellingParser)getSource()</code>.
	 *
	 * @return The parser.
	 */
	public SpellingParser getParser() {
		return (SpellingParser)getSource();
	}


	/**
	 * Returns the text area that was parsed.
	 *
	 * @return The text area.
	 */
	public RSyntaxTextArea getTextArea() {
		return textArea;
	}


	/**
	 * Returns the type of this event.
	 *
	 * @return Either {@link #WORD_ADDED} or {@link #WORD_IGNORED}.
	 */
	public int getType() {
		return type;
	}


	/**
	 * Returns the word being added or ignored.
	 *
	 * @return The word.
	 */
	public String getWord() {
		return word;
	}


	/**
	 * Sets the type of event being fired.
	 *
	 * @param type The type of event being fired.
	 */
	private void setType(int type) {
		if (type!=WORD_ADDED && type!=WORD_IGNORED) {
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		this.type = type;
	}


}