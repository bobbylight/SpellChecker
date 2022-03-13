/*
 * 03/19/2014
 *
 * DefaultSpellCheckableTokenIdentifier.java - Identifies comment tokens to
 * be spell checked.
 * 
 * This library is distributed under the LGPL.  See the included
 * LICENSE.md file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;


/**
 * The spell-checkable token identifier used by {@link SpellingParser} if
 * none is explicitly identified.  It causes all comment tokens to be
 * spell checked.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DefaultSpellCheckableTokenIdentifier
		implements SpellCheckableTokenIdentifier {


	/**
	 * The default implementation of this method does nothing; this token
	 * identifier does not have state.
	 */
	@Override
	public void begin() {
	}


	/**
	 * The default implementation of this method does nothing; this token
	 * identifier does not have state.
	 */
	@Override
	public void end() {
	}


	/**
	 * Returns <code>true</code> if the token is a comment.
	 *
	 * @return <code>true</code> only if the token is a comment.
	 */
	@Override
	public boolean isSpellCheckable(Token t) {
		// COMMENT_MARKUP represents e.g. Javadoc markup like
		// "<pre>method()</pre>", thus shouldn't be spellchecked.
		// MARKUP_COMMENT, however represents comments in markup
		// languages, and so should be spellchecked.
		return t.isComment() && TokenTypes.COMMENT_MARKUP != t.getType();
	}


}
