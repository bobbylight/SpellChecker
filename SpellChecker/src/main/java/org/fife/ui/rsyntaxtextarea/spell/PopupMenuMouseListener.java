package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.com.swabunga.spell.engine.Word;
import org.fife.com.swabunga.spell.event.SpellChecker;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

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

    public PopupMenuMouseListener(RSyntaxTextArea textArea, MouseListener[] listeners, SpellChecker spellChecker){
        this.textArea = textArea;
        this.listeners = listeners;
        this.spellChecker = spellChecker;
        document = (RSyntaxDocument) textArea.getDocument();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Arrays.stream(listeners).forEach(m -> m.mouseClicked(e));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(int i = 0; i < count; i++){
            textArea.getPopupMenu().remove(0);
        }
        count = 0;

        int offset = textArea.viewToModel(e.getPoint());
        try {
            int start = Utilities.getWordStart(textArea, offset);
            int end = Utilities.getWordEnd(textArea, offset);
            String word = textArea.getText(start, end - start);
            if(!spellChecker.isCorrect(word)) {
                List<Word> words = spellChecker.getSuggestions(word, 10);
                count = words.size();

                words.stream().forEach(w -> {
                    JMenuItem item = new JMenuItem(w.getWord());
                    item.addActionListener(src -> {
                        try {
                            document.remove(start, end - start);
                            document.insertString(start, w.getWord(), null);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    });
                    textArea.getPopupMenu().insert(item, 0);
                });
            }

        }catch(Exception ee){
            ee.printStackTrace();
        }
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
}
