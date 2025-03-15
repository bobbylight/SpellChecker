package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BasicSpellCheckEvent}.
 */
class BasicSpellCheckEventTest {

    private BasicSpellCheckEvent event;
    private String invalidWord;

    @BeforeEach
    void setUp() {
        invalidWord = "mispelled";
        event = new BasicSpellCheckEvent(invalidWord, 0);
    }

    @Test
    void testConstructor_wordTokenizer() {
        assertDoesNotThrow(() -> {
            StringWordTokenizer tokenizer = new StringWordTokenizer("This");
            tokenizer.nextWord();
            new BasicSpellCheckEvent(invalidWord, tokenizer);
        });
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
}
