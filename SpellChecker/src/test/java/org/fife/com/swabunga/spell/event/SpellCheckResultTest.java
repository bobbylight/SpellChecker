package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code SpellCheckResult} class.
 */
class SpellCheckResultTest {

    @Test
    void testConstructor_noErrors() {
        SpellCheckResult result = new SpellCheckResult(0, false);
        assertEquals(0, result.getErrorCount());
        assertFalse(result.hasErrors());
        assertFalse(result.isCancelled());
    }

    @Test
    void testConstructor_errors() {
        SpellCheckResult result = new SpellCheckResult(3, false);
        assertEquals(3, result.getErrorCount());
        assertTrue(result.hasErrors());
        assertFalse(result.isCancelled());
    }

    @Test
    void testConstructor_cancelled() {
        SpellCheckResult result = new SpellCheckResult(2, true);
        assertEquals(2, result.getErrorCount());
        assertTrue(result.hasErrors());
        assertTrue(result.isCancelled());
    }

    @Test
    void testConstructor_negativeErrorCount() {
        assertThrows(IllegalArgumentException.class, () -> new SpellCheckResult(-1, false));
    }

    @Test
    void testToString() {
        assertEquals("SpellCheckResult[errorCount=2, cancelled=true]", new SpellCheckResult(2, true).toString());
    }
}
