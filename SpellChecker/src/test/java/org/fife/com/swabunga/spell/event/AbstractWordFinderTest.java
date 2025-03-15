package org.fife.com.swabunga.spell.event;

import org.junit.jupiter.api.Test;

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

    private static final class BrokenWordFinder extends AbstractWordFinder {

        @Override
        public Word next() {
            currentWord = nextWord = null;
            return null;
        }
    }
}
