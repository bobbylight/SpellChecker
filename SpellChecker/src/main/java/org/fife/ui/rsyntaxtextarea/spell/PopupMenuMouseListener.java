package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.engine.Word;
import org.fife.com.swabunga.spell.event.SpellChecker;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.spell.event.SpellingParserEvent;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;

public class PopupMenuMouseListener implements MouseListener {
    private int count = -1;
    private RSyntaxTextArea textArea;
    private MouseListener[] listeners;
    private RSyntaxDocument document;
    private SpellChecker spellChecker;
    private SpellingParser parser;
    private String word = "";

    private JMenuItem addWord = new JMenuItem("Add word");
    private JMenuItem ignoreWord = new JMenuItem("Ignore word");

    public PopupMenuMouseListener(SpellingParser parser, RSyntaxTextArea textArea, MouseListener[] listeners, SpellChecker spellChecker){
        this.textArea = textArea;
        this.listeners = listeners;
        this.spellChecker = spellChecker;
        this.parser = parser;
        document = (RSyntaxDocument) textArea.getDocument();

        addWord.addActionListener(src -> {
            addWordToUserDictionary(word);
        });

        ignoreWord.addActionListener(src -> {
            addWordToUserDictionary(word);
        });

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Arrays.stream(listeners).forEach(m -> m.mouseClicked(e));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()) {

            for (int i = 0; i < count; i++) {
                textArea.getPopupMenu().remove(0);
            }
            count = 0;

            int offset = textArea.viewToModel(e.getPoint());
            try {
                int start = Utilities.getWordStart(textArea, offset);
                int end = Utilities.getWordEnd(textArea, offset);
                word = textArea.getText(start, end - start);
                if (!spellChecker.isCorrect(word)) {
                    List<Word> words = spellChecker.getSuggestions(word, 10);
                    count = words.size() + 1;

                    words.stream().forEach(w -> {
                        JMenuItem item = new JMenuItem(w.getWord());
                        item.addActionListener(src -> {
                            try {
                                textArea.beginAtomicEdit();
                                document.replace(start, end - start, w.getWord(), null);
                                textArea.endAtomicEdit();
                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                        });
                        textArea.getPopupMenu().insert(item, 0);
                    });
                    textArea.getPopupMenu().insert(new JPopupMenu.Separator(), count - 1);
                    if(parser.getAllowAdd() || parser.getAllowIgnore()){
                        if(parser.getAllowAdd()){
                            textArea.getPopupMenu().insert(addWord, count);

                            count++;
                        }
                        if(parser.getAllowIgnore()){
                            textArea.getPopupMenu().insert(ignoreWord, count);
                            count++;
                        }
                        count++;
                        textArea.getPopupMenu().insert(new JPopupMenu.Separator(), count - 1);
                    }
                }

            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        //Allow anyone else to get their listeners processed.
        Arrays.stream(listeners).forEach(m -> m.mousePressed(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Arrays.stream(listeners).forEach(m -> m.mouseReleased(e));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Arrays.stream(listeners).forEach(m -> m.mouseEntered(e));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Arrays.stream(listeners).forEach(m -> m.mouseExited(e));
    }

    private void addWordToUserDictionary(String word){
        if (parser.getUserDictionary() == null) {
            // TODO: Add callback for application to prompt to create
            // a user dictionary
            UIManager.getLookAndFeel().provideErrorFeedback(textArea);
        }

        if (spellChecker.addToDictionary(word)) {
            textArea.forceReparsing(parser);
            SpellingParserEvent se = new SpellingParserEvent(parser,
                    textArea, SpellingParserEvent.WORD_ADDED, word);
            parser.fireSpellingParserEvent(se);
        } else { // IO error adding the word
            UIManager.getLookAndFeel().provideErrorFeedback(textArea);
        }
    }

    public void ignoreWord(String word){
        spellChecker.ignoreAll(word);
        textArea.forceReparsing(parser);
        SpellingParserEvent se = new SpellingParserEvent(parser,
                textArea, SpellingParserEvent.WORD_IGNORED, word);
        parser.fireSpellingParserEvent(se);
    }
}
