package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link AbstractWordFinder}.
 */
class AbstractWordFinderTest {

    @Test
    void testCurrent_throwsIfNoCurrentWord() {
        assertThrows(WordNotFoundException.class, () ->
            new BrokenWordFinder().current()
        );
    }

    @Test
    void testReplace_throwsIfNoCurrentWord() {
        assertThrows(WordNotFoundException.class, () ->
            new BrokenWordFinder().replace("foo")
        );
    }

    @Test
    void testStartsSentence_throwsIfNoCurrentWord() {
        assertThrows(WordNotFoundException.class, () ->
            new BrokenWordFinder().startsSentence()
        );
    }

    @Test
    void testIgnore_startAndEndChars() {
        IgnoreTestWordFinder finder = new IgnoreTestWordFinder("ab <tag> cd");
        assertEquals(8, finder.ignore(3, '<', '>'));
    }

    @Test
    void testIgnore_startAndEndChars_noEndCharFound() {
        IgnoreTestWordFinder finder = new IgnoreTestWordFinder("ab <tag cd");
        assertEquals(10, finder.ignore(3, '<', '>'));
    }

    @Test
    void testIgnore_startAndEndChars_startCharNotAtIndex() {
        IgnoreTestWordFinder finder = new IgnoreTestWordFinder("ab <tag> cd");
        assertEquals(2, finder.ignore(2, '<', '>'));
    }

    @Test
    void testIgnore_startCharOnly_stopsAtNonLetterOrDigit() {
        IgnoreTestWordFinder finder = new IgnoreTestWordFinder("ab #tag1 cd");
        assertEquals(8, finder.ignore(3, '#'));
    }

    @Test
    void testIgnore_startCharOnly_toEndOfText() {
        IgnoreTestWordFinder finder = new IgnoreTestWordFinder("ab #tag1");
        assertEquals(8, finder.ignore(3, '#'));
    }

    @Test
    void testIgnore_indexAtEndOfText() {
        IgnoreTestWordFinder finder = new IgnoreTestWordFinder("ab");
        assertEquals(2, finder.ignore(2, '<', '>'));
    }

    /**
     * Exposes {@code ignore()} for testing.
     */
    private static final class IgnoreTestWordFinder extends AbstractWordFinder {

        IgnoreTestWordFinder(String text) {
            super(text);
        }

        @Override
        public Word next() {
            return null;
        }
    }

    private static final class BrokenWordFinder extends AbstractWordFinder {

        @Override
        public Word next() {
            currentWord = nextWord = null;
            return null;
        }
    }
}
