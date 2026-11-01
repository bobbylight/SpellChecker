package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link EditDistance}.
 */
class EditDistanceTest {

    private Configuration config;
    private EditDistance editDistance;

    @BeforeEach
    void setUp() {
        config = Configuration.getConfiguration();
        editDistance = new EditDistance(config);
    }

    @Test
    void testGetDistance_identicalWords() {
        Assertions.assertEquals(0, editDistance.getDistance("test", "test"));
    }

    @Test
    void testGetDistance_caseInsensitive_firstChar() {
        Assertions.assertEquals(config.getInteger(Configuration.COST_CHANGE_CASE),
                editDistance.getDistance("test", "Test"));
    }

    @Test
    void testGetDistance_caseInsensitive_nonFirstChar() {
        Assertions.assertEquals(config.getInteger(Configuration.COST_CHANGE_CASE),
                editDistance.getDistance("test", "tEst"));
    }

    @Test
    void testGetDistance_differentWords() {
        int expectedDistance = config.getInteger(Configuration.COST_SUBST_CHARS);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("test", "tent"));
    }

    @Test
    void testGetDistance_swappedCharacters() {
        int expectedDistance = config.getInteger(Configuration.COST_SWAP_CHARS);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("abcd", "abdc"));
    }

    @Test
    void testGetDistance_insertCharacter() {
        int expectedDistance = config.getInteger(Configuration.COST_INSERT_CHAR);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("test", "tests"));
    }

    @Test
    void testGetDistance_deleteCharacter() {
        int expectedDistance = config.getInteger(Configuration.COST_REMOVE_CHAR);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("tests", "test"));
    }

    @Test
    void testGetDistance_usesConfiguredCosts() {
        config.setInteger(Configuration.COST_SUBST_CHARS, 7);
        Assertions.assertEquals(7, new EditDistance(config).getDistance("test", "tent"));
    }

    @Test
    void testGetDistance_costsReadAtConstruction() {
        int expectedDistance = config.getInteger(Configuration.COST_SUBST_CHARS);
        config.setInteger(Configuration.COST_SUBST_CHARS, 7);
        Assertions.assertEquals(expectedDistance, editDistance.getDistance("test", "tent"));
    }
}
