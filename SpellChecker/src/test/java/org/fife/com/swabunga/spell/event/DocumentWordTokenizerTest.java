package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code DocumentWordTokenizer}.
 */
class DocumentWordTokenizerTest {

    private DocumentWordTokenizer tokenizer;
    private PlainDocument doc;

    @BeforeEach
    void setUp() throws BadLocationException {
        doc = new PlainDocument();
        doc.insertString(0, "This is a test document.", null);
        tokenizer = new DocumentWordTokenizer(doc);
    }

    @Test
    void testGetCurrentWordCount() {
        assertEquals(0, tokenizer.getCurrentWordCount());
        assertEquals("This", tokenizer.nextWord());
        assertEquals(1, tokenizer.getCurrentWordCount());
        assertEquals("is", tokenizer.nextWord());
        assertEquals(2, tokenizer.getCurrentWordCount());
        assertEquals("a", tokenizer.nextWord());
        assertEquals(3, tokenizer.getCurrentWordCount());
        assertEquals("test", tokenizer.nextWord());
        assertEquals(4, tokenizer.getCurrentWordCount());
        assertEquals("document", tokenizer.nextWord());
        assertEquals(5, tokenizer.getCurrentWordCount());
    }

    @Test
    void testHasMoreWords_emptyDocument() {
        doc = new PlainDocument();
        tokenizer = new DocumentWordTokenizer(doc);
        assertFalse(tokenizer.hasMoreWords());
    }

    @Test
    void testHasMoreWords_initialState() {
        assertTrue(tokenizer.hasMoreWords());
    }

    @Test
    void testNextWord() {
        assertEquals("This", tokenizer.nextWord());
        assertEquals("is", tokenizer.nextWord());
        assertEquals("a", tokenizer.nextWord());
        assertEquals("test", tokenizer.nextWord());
        assertEquals("document", tokenizer.nextWord());
        assertFalse(tokenizer.hasMoreWords());
    }

    @Test
    void testNextWord_hyphenatedWord() throws BadLocationException {
        doc.replace(0, doc.getLength(), "mid-level", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("mid-level", tokenizer.nextWord());
        assertFalse(tokenizer.hasMoreWords());
    }

    @Test
    void testNextWord_apostrophe() throws BadLocationException {
        doc.replace(0, doc.getLength(), "doesn't", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("doesn't", tokenizer.nextWord());
        assertFalse(tokenizer.hasMoreWords());
    }

    @Test
    void testNextWord_apostrophe_thenEndOfFile() throws BadLocationException {
        doc.replace(0, doc.getLength(), "doesn'", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("doesn", tokenizer.nextWord());
        assertFalse(tokenizer.hasMoreWords());
    }

    @Test
    void testNextWord_apostrophe_thenNonWordChar() throws BadLocationException {
        doc.replace(0, doc.getLength(), "doesn'+", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("doesn", tokenizer.nextWord());
        assertFalse(tokenizer.hasMoreWords());
    }

    @Test
    void testHasMoreWords_afterIteration() {
        while (tokenizer.hasMoreWords()) {
            tokenizer.nextWord();
        }
        assertFalse(tokenizer.hasMoreWords());
    }

    @Test
    void testReplaceWord() throws BadLocationException {
        tokenizer.nextWord(); // Move to "This"
        tokenizer.replaceWord("That");
        assertEquals("That is a test document.", doc.getText(0, doc.getLength()));
    }

    @Test
    void testGetContext() {
        tokenizer.nextWord();
        assertEquals("This is a test document.", tokenizer.getContext());
    }

    @Test
    void testGetCurrentWordPosition() {
        tokenizer.nextWord(); // Move to "This"
        assertEquals(0, tokenizer.getCurrentWordPosition());
    }

    @Test
    void testGetCurrentWordEnd() {
        tokenizer.nextWord(); // Move to "This"
        assertEquals(4, tokenizer.getCurrentWordEnd());
    }

    @Test
    void testIsNewSentence() {
        assertTrue(tokenizer.isNewSentence());
        tokenizer.nextWord(); // Move to "This"
        tokenizer.nextWord(); // Move to "is"
        assertFalse(tokenizer.isNewSentence());
    }

    @Test
    void testPosStartFullWordFrom_offset0() {
        tokenizer.posStartFullWordFrom(0);
        assertEquals("This", tokenizer.nextWord());
    }

    @Test
    void testPosStartFullWordFrom_nonZeroOffset() {
        tokenizer.posStartFullWordFrom(12);
        assertEquals("test", tokenizer.nextWord());
    }

    @Test
    void testPosStartFullWordFrom_apostrophe() throws BadLocationException {
        doc.replace(0, doc.getLength(), "foo doesn't bar", null);
        tokenizer = new DocumentWordTokenizer(doc);
        tokenizer.posStartFullWordFrom(10);
        assertEquals("doesn't", tokenizer.nextWord());
    }

    @Test
    void testPosStartFullWordFrom_hyphen() throws BadLocationException {
        doc.replace(0, doc.getLength(), "foo mid-level bar", null);
        tokenizer = new DocumentWordTokenizer(doc);
        tokenizer.posStartFullWordFrom(11);
        assertEquals("mid-level", tokenizer.nextWord());
    }
}
