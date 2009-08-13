/*
 * 07/21/2009
 *
 * SpellingParserDemo.java - Demo of the spell-checker for RSyntaxTextArea.
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
package org.fife.ui.rsyntaxtextarea.spell.demo;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


/**
 * Demo application for the <code>SpellingParser</code> add-on to RSTA.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SpellingParserDemo extends JFrame {


	/**
	 * Constructor.
	 */
	public SpellingParserDemo() {
		setRootPane(new DemoRootPane());
		setTitle("Spelling Parser Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}


	/**
	 * Program entry point.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					String laf = UIManager.getSystemLookAndFeelClassName();
laf = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
					UIManager.setLookAndFeel(laf);
				} catch (RuntimeException re) { // FindBugs
					throw re;
				} catch (Exception e) {
					e.printStackTrace();
				}
				new SpellingParserDemo().setVisible(true);
			}
		});

	}


}