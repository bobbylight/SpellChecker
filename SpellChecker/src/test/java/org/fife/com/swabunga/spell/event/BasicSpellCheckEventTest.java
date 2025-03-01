package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BasicSpellCheckEvent}.
 */
class BasicSpellCheckEventTest {

    private BasicSpellCheckEvent event;
    private List<Word> suggestions;
    private String invalidWord;

    @BeforeEach
    void setUp() {
        invalidWord = "mispelled";
        suggestions = Collections.singletonList(new Word("misspelled", 0));
        event = new BasicSpellCheckEvent(invalidWord, suggestions, 0);
    }

    @Test
    void testConstructor_wordTokenizer() {
        assertDoesNotThrow(() -> {
            StringWordTokenizer tokenizer = new StringWordTokenizer("This");
            tokenizer.nextWord();
            new BasicSpellCheckEvent(invalidWord, suggestions, tokenizer);
        });
    }

    @Test
    void testGetSuggestions() {
        assertEquals(suggestions, event.getSuggestions());
    }

    @Test
    void testGetInvalidWord() {
        assertEquals(invalidWord, event.getInvalidWord());
    }

    @Test
    void testGetWordContext() {
        assertNull(event.getWordContext());
    }

    @Test
    void testGetWordContextPosition() {
        assertEquals(0, event.getWordContextPosition());
    }

    @Test
    void testGetAction_initial() {
        assertEquals(BasicSpellCheckEvent.INITIAL, event.getAction());
    }

    @Test
    void testReplaceWord() {
        event.replaceWord("corrected", false);
        assertEquals(BasicSpellCheckEvent.REPLACE, event.getAction());
        assertEquals("corrected", event.getReplaceWord());
    }

    @Test
    void testReplaceWord_replaceAll() {
        event.replaceWord("corrected", true);
        assertEquals(BasicSpellCheckEvent.REPLACEALL, event.getAction());
        assertEquals("corrected", event.getReplaceWord());
    }

    @Test
    void testReplaceWord_actionCanOnlyBeSetOnce() {
        event.replaceWord("corrected", false);
        assertEquals(BasicSpellCheckEvent.REPLACE, event.getAction());
        assertThrows(IllegalStateException.class, () -> event.replaceWord("corrected", true));
    }

    @Test
    void testIgnoreWord() {
        event.ignoreWord(false);
        assertEquals(BasicSpellCheckEvent.IGNORE, event.getAction());
    }

    @Test
    void testIgnoreWord_ignoreAll() {
        event.ignoreWord(true);
        assertEquals(BasicSpellCheckEvent.IGNOREALL, event.getAction());
    }

    @Test
    void testIgnoreWord_actionCanOnlyBeSetOnce() {
        event.replaceWord("corrected", false);
        assertThrows(IllegalStateException.class, () -> event.ignoreWord(false));
    }

    @Test
    void testAddToDictionary() {
        event.addToDictionary("newword");
        assertEquals(BasicSpellCheckEvent.ADDTODICT, event.getAction());
        assertEquals("newword", event.getReplaceWord());
    }

    @Test
    void testAddToDictionary_actionCanOnlyBeSetOnce() {
        event.addToDictionary("newword");
        assertThrows(IllegalStateException.class, () -> event.addToDictionary("newword"));
    }

    @Test
    void testCancel() {
        event.cancel();
        assertEquals(BasicSpellCheckEvent.CANCEL, event.getAction());
    }

    @Test
    void testCancel_actionCanOnlyBeSetOnce() {
        event.cancel();
        assertThrows(IllegalStateException.class, () -> event.cancel());
    }
}
