package org.fife.ui.rsyntaxtextarea.spell.demo;

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

	private static final String INPUT_FILE	= "Input.java";


	public DemoRootPane() {
		textArea = createTextArea();
		scrollPane = new RTextScrollPane(textArea, true);
		getContentPane().add(scrollPane);
		setJMenuBar(createMenuBar());
	}


	/**
	 * Starts a thread to load the spell checker when the app is made visible,
	 * since the dictionary is somewhat large (takes 0.9 seconds to load on
	 * a 3.0 GHz Core 2 Duo).<p>
	 * This assumes the app will only be made visible once, which is certainly
	 * true for our demo.
	 *
	 * @param visible Whether the root pane should be made visible.
	 */
	public void addNotify() {
		super.addNotify();
		new Thread() {
			public void run() {
				final SpellingParser parser = createSpellingParser();
				if (parser!=null) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							textArea.addParser(parser);
						}
					});
				}
			}
		}.start();
	}


	private JMenuBar createMenuBar() {

		JMenuBar mb = new JMenuBar();

		JMenu menu = new JMenu("Help");
		JMenuItem item = new JMenuItem(new AboutAction());
		menu.add(item);
		mb.add(menu);

		return mb;

	}


	private SpellingParser createSpellingParser() {
		File zip = new File("distfiles/english_dic.zip");
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
		textArea.addHyperlinkListener(this);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		return textArea;
	}


	void focusTextArea() {
		textArea.requestFocusInWindow();
	}


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

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(DemoRootPane.this,
					"<html><b>Spell Checker</b> - An add-on for RSyntaxTextArea" +
					"<br>that does spell checking in code comments." +
					"<br>Version 1.3" +
					"<br>Licensed under the LGPL",
					"About Spell Checker",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}


}