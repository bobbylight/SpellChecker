package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WordNotFoundException}.
 */
class WordNotFoundExceptionTest {

    @Test
    void testDefaultConstructor() {
        WordNotFoundException e = new WordNotFoundException();
        assertNull(e.getMessage());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Word not found";
        WordNotFoundException e = new WordNotFoundException(message);
        assertEquals(message, e.getMessage());
    }
}
