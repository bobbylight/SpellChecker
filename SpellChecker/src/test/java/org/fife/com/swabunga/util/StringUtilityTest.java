package org.fife.com.swabunga.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StringUtility}.
 */
public class StringUtilityTest {

    @Test
    void testReplace_happyPath() {
        StringBuilder sb = new StringBuilder("Hello world");
        String replaceWith = "foobar";
        StringBuilder result = StringUtility.replace(sb, 3, 5, replaceWith);
        assertEquals("Helfoobar world", result.toString());
    }

    @Test
    void testReplace_emptyReplaceWith() {
        StringBuilder sb = new StringBuilder("Hello world");
        String replaceWith = "";
        StringBuilder result = StringUtility.replace(sb, 3, 5, replaceWith);
        assertEquals("Hel world", result.toString());
    }

    @Test
    void testReplace_entireString() {
        StringBuilder sb = new StringBuilder("Hello world");
        String replaceWith = "Hi";
        StringBuilder result = StringUtility.replace(sb, 0, sb.length(), replaceWith);
        assertEquals("Hi", result.toString());
    }

    @Test
    void testReplace_noChange() {
        StringBuilder sb = new StringBuilder("Hello world");
        String replaceWith = "lo";
        StringBuilder result = StringUtility.replace(sb, 3, 5, replaceWith);
        assertEquals("Hello world", result.toString());
    }

    @Test
    void testReplace_insertInsteadOfReplace() {
        StringBuilder sb = new StringBuilder("Hello world");
        String replaceWith = "ADDED";
        StringBuilder result = StringUtility.replace(sb, 3, 3, replaceWith);
        assertEquals("HelADDEDlo world", result.toString());
    }
}
