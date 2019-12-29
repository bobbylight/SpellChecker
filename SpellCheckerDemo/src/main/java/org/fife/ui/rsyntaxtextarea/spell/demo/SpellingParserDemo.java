/*
 * 07/21/2009
 *
 * SpellingParserDemo.java - Demo of the spell-checker for RSyntaxTextArea.
 * 
 * This library is distributed under the LGPL.  See the included
 * SpellChecker.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell.demo;

import javax.swing.*;


/**
 * Demo application for the <code>SpellingParser</code> add-on to RSTA.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class SpellingParserDemo extends JFrame {


	/**
	 * Constructor.
	 */
	private SpellingParserDemo() {
		setRootPane(new DemoRootPane());
		setTitle("Spelling Parser Demo");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}


	/**
	 * Program entry point.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
			try {
				String laf = UIManager.getSystemLookAndFeelClassName();
//laf = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
				UIManager.setLookAndFeel(laf);
			} catch (RuntimeException re) { // FindBugs
				throw re;
			} catch (Exception e) {
				e.printStackTrace();
			}
			new SpellingParserDemo().setVisible(true);
		});

	}


}
