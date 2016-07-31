/*
 * 07/21/2009
 *
 * DemoRootPane.java - Root pane for the demo.
 * 
 * This library is distributed under the LGPL.  See the included
 * SpellChecker.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell.demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.*;

import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;


/**
 * The root pane used by the demo.  This allows both the applet and the
 * stand-alone application to share the same UI. 
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DemoRootPane extends JRootPane implements HyperlinkListener,
											SyntaxConstants {

	private RTextScrollPane scrollPane;
	private RSyntaxTextArea textArea;
	private SpellingParser parser;
	private ToggleSpellCheckingAction toggleAction;

	private static final String INPUT_FILE	= "Input.java";


	public DemoRootPane() {
		textArea = createTextArea();
		scrollPane = new RTextScrollPane(textArea, true);
		ErrorStrip es = new ErrorStrip(textArea);
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(scrollPane);
		temp.add(es, BorderLayout.LINE_END);
		getContentPane().add(temp);
		setJMenuBar(createMenuBar());

	}


	/**
	 * Starts a thread to load the spell checker when the app is made visible,
	 * since the dictionary is somewhat large (takes 0.9 seconds to load on
	 * a 3.0 GHz Core 2 Duo).<p>
	 * This assumes the app will only be made visible once, which is certainly
	 * true for our demo.
	 */
	@Override
	public void addNotify() {
		super.addNotify();
		new Thread() {
			@Override
			public void run() {
				parser = createSpellingParser();
				if (parser!=null) {
					try {
						File userDict= File.createTempFile("spellDemo", ".txt");
						parser.setUserDictionary(userDict);
						System.out.println("User dictionary: " +
											userDict.getAbsolutePath());
					} catch (IOException ioe) { // Applets, IO errors
						System.err.println("Can't open user dictionary: " +
									ioe.getMessage());
					} catch (SecurityException se) { // Applets
						System.err.println("Can't open user dictionary: " +
								se.getMessage());
					}
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							textArea.addParser(parser);
							toggleAction.setEnabled(true);
						}
					});
				}
			}
		}.start();
	}


	private JMenuBar createMenuBar() {

		JMenuBar mb = new JMenuBar();

		JMenu menu = new JMenu("Options");
		toggleAction = new ToggleSpellCheckingAction();
		toggleAction.setEnabled(false);
		JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem(toggleAction);
		cbItem.setSelected(true);
		menu.add(cbItem);
		menu.addSeparator();
		JMenuItem item = new JMenuItem(new AboutAction());
		menu.add(item);
		mb.add(menu);

		return mb;

	}


	private SpellingParser createSpellingParser() {
		File zip = new File("src/main/dist/english_dic.zip");
		try {
			return SpellingParser.createEnglishSpellingParser(zip, true);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}


	private RSyntaxTextArea createTextArea() {
		RSyntaxTextArea textArea = new RSyntaxTextArea(25, 70);
		textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
		ClassLoader cl = getClass().getClassLoader();
		InputStream in = cl.getResourceAsStream(INPUT_FILE);
		try {
			BufferedReader r = null;
			if (in!=null) {
				r = new BufferedReader(new InputStreamReader(in));
			}
			else {
				r = new BufferedReader(new FileReader(INPUT_FILE));
			}
			textArea.read(r, null);
			r.close();
		} catch (IOException ioe) {
			textArea.setText("// Type Java source.  Comments are spell checked.");
		}
		textArea.setCaretPosition(0);
		textArea.discardAllEdits();
		textArea.addHyperlinkListener(this);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		return textArea;
	}


	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
			URL url = e.getURL();
			if (url==null) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			}
			else {
				JOptionPane.showMessageDialog(this,
									"URL clicked:\n" + url.toString());
			}
		}
	}


	private class AboutAction extends AbstractAction {

		public AboutAction() {
			putValue(NAME, "About Spell Checker...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DemoRootPane.this,
				"<html><b>Spell Checker</b> - An add-on for RSyntaxTextArea" +
				"<br>that does spell checking in code comments." +
				"<br>Version 2.5.4" +
				"<br>Licensed under the LGPL",
				"About Spell Checker",
				JOptionPane.INFORMATION_MESSAGE);
		}

	}


	private class ToggleSpellCheckingAction extends AbstractAction {

		private boolean enabled;

		public ToggleSpellCheckingAction() {
			putValue(NAME, "Toggle Spell Checking");
			enabled = true;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			enabled = !enabled;
			if (enabled) {
				textArea.addParser(parser);
			}
			else {
				textArea.removeParser(parser);
			}
		}

	}


}