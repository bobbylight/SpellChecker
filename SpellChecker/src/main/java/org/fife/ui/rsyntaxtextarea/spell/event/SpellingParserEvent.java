/*
 * 08/29/2009
 *
 * SpellingParserEvent.java - An event fired by the spelling parser.
 *
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
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
	 * The types of spelling parser events.
	 */
	public enum Type {

		/**
		 * A word was added to the user's dictionary.
		 */
		WORD_ADDED,

		/**
		 * A word will be ignored for the rest of this JVM session.
		 */
		WORD_IGNORED
	}

	private final RSyntaxTextArea textArea;
	private final Type type;
	private final String word;


	/**
	 * Constructor.
	 *
	 * @param source The source parser.
	 * @param textArea The text area that was parsed.
	 * @param type The type of event.  This cannot be <code>null</code>.
	 * @param word The word being added or ignored.
	 * @throws IllegalArgumentException If <code>type</code> is
	 *         <code>null</code>.
	 */
	public SpellingParserEvent(SpellingParser source, RSyntaxTextArea textArea,
								Type type, String word) {
		super(source);
		if (type == null) {
			throw new IllegalArgumentException("type cannot be null");
		}
		this.textArea = textArea;
		this.type = type;
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
	 * @return The type of this event.
	 */
	public Type getType() {
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


}
