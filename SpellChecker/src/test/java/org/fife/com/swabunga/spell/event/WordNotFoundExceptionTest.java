package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WordNotFoundException}.
 */
class WordNotFoundExceptionTest {

    @Test
    void testDefaultConstructor() {
        var e = new WordNotFoundException();
        assertNull(e.getMessage());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Word not found";
        var e = new WordNotFoundException(message);
        assertEquals(message, e.getMessage());
    }
}
