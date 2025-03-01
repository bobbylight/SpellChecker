package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EditDistance}.
 */
class EditDistanceTest {

    @Test
    void testGetDistance_identicalWords() {
        Assertions.assertEquals(0, EditDistance.getDistance("test", "test"));
    }

    @Test
    void testGetDistance_caseInsensitive_firstChar() {
        Assertions.assertEquals(EditDistance.CONFIG.getInteger(Configuration.COST_CHANGE_CASE),
                EditDistance.getDistance("test", "Test"));
    }

    @Test
    void testGetDistance_caseInsensitive_nonFirstChar() {
        Assertions.assertEquals(EditDistance.CONFIG.getInteger(Configuration.COST_CHANGE_CASE),
                EditDistance.getDistance("test", "tEst"));
    }

    @Test
    void testGetDistance_differentWords() {
        int expectedDistance = EditDistance.CONFIG.getInteger(Configuration.COST_SUBST_CHARS);
        Assertions.assertEquals(expectedDistance, EditDistance.getDistance("test", "tent"));
    }

    @Test
    void testGetDistance_swappedCharacters() {
        int expectedDistance = EditDistance.CONFIG.getInteger(Configuration.COST_SWAP_CHARS);
        Assertions.assertEquals(expectedDistance, EditDistance.getDistance("abcd", "abdc"));
    }

    @Test
    void testGetDistance_insertCharacter() {
        int expectedDistance = EditDistance.CONFIG.getInteger(Configuration.COST_INSERT_CHAR);
        Assertions.assertEquals(expectedDistance, EditDistance.getDistance("test", "tests"));
    }

    @Test
    void testGetDistance_deleteCharacter() {
        int expectedDistance = EditDistance.CONFIG.getInteger(Configuration.COST_REMOVE_CHAR);
        Assertions.assertEquals(expectedDistance, EditDistance.getDistance("tests", "test"));
    }
}
