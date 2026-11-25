package org.fife.com.swabunga.spell.event;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testIsNewSentence_segmentWithNonZeroOffset() throws BadLocationException {
        // Removing text at the start of a document moves its gap buffer to
        // offset 0, so the Segment the tokenizer reads has a non-zero offset
        doc.replace(0, doc.getLength(), "XEnd here. Next one", null);
        doc.remove(0, 1);
        var segment = new Segment();
        doc.getText(0, doc.getLength(), segment);
        assertNotEquals(0, segment.offset, "Test precondition not met");

        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("End", tokenizer.nextWord());
        assertTrue(tokenizer.isNewSentence());
        assertEquals("here", tokenizer.nextWord());
        assertFalse(tokenizer.isNewSentence());
        assertEquals("Next", tokenizer.nextWord());
        assertTrue(tokenizer.isNewSentence());
    }

    @Test
    void testIsNewSentence_afterQuestionMark_lowerCase() throws BadLocationException {
        doc.replace(0, doc.getLength(), "Is it? yes it is", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("Is", tokenizer.nextWord());
        assertEquals("it", tokenizer.nextWord());
        assertEquals("yes", tokenizer.nextWord());
        assertTrue(tokenizer.isNewSentence());
        assertEquals("it", tokenizer.nextWord());
        assertFalse(tokenizer.isNewSentence());
    }

    @Test
    void testIsNewSentence_afterExclamationMark_lowerCase() throws BadLocationException {
        doc.replace(0, doc.getLength(), "Stop! now please", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("Stop", tokenizer.nextWord());
        assertEquals("now", tokenizer.nextWord());
        assertTrue(tokenizer.isNewSentence());
        assertEquals("please", tokenizer.nextWord());
        assertFalse(tokenizer.isNewSentence());
    }

    @Test
    void testIsNewSentence_afterPeriod_lowerCase() throws BadLocationException {
        doc.replace(0, doc.getLength(), "The end. then more", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("The", tokenizer.nextWord());
        assertEquals("end", tokenizer.nextWord());
        assertEquals("then", tokenizer.nextWord());
        assertTrue(tokenizer.isNewSentence());
        assertEquals("more", tokenizer.nextWord());
        assertFalse(tokenizer.isNewSentence());
    }

    @Test
    void testIsNewSentence_questionMarkInsideWordRun() throws BadLocationException {
        doc.replace(0, doc.getLength(), "what?no", null);
        tokenizer = new DocumentWordTokenizer(doc);
        assertEquals("what", tokenizer.nextWord());
        assertEquals("no", tokenizer.nextWord());
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
