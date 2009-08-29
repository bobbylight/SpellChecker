/*
 * 08/29/2009
 *
 * SpellingParserListener.java - Listens for events from a spelling parser.
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

import java.util.EventListener;

import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;


/**
 * Listens for events from a {@link SpellingParser}.  A listener of this type
 * will receive notification when:
 * 
 * <ul>
 *    <li>A word is added to the user's dictionary.</li>
 *    <li>A word will be ignored for the rest of the JVM session.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface SpellingParserListener extends EventListener {


	/**
	 * Called when an event occurs in the spelling parser.
	 *
	 * @param e The event.
	 */
	public void spellingParserEvent(SpellingParserEvent e);


}