package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PropertyConfiguration}.
 */
class PropertyConfigurationTest {

    private PropertyConfiguration config;

    @BeforeEach
    void setUp() {
        config = new PropertyConfiguration();
    }

    @Test
    void testGetBoolean() {
        assertTrue(config.getBoolean(Configuration.SPELL_IGNOREUPPERCASE));
        assertFalse(config.getBoolean(Configuration.SPELL_IGNOREMIXEDCASE));
    }

    @Test
    void testGetInteger() {
        assertEquals(95, config.getInteger(Configuration.COST_REMOVE_CHAR));
        assertEquals(95, config.getInteger(Configuration.COST_INSERT_CHAR));
    }

    @Test
    void testSetBoolean() {
        config.setBoolean(Configuration.SPELL_IGNOREUPPERCASE, false);
        assertFalse(config.getBoolean(Configuration.SPELL_IGNOREUPPERCASE));

        config.setBoolean(Configuration.SPELL_IGNOREMIXEDCASE, true);
        assertTrue(config.getBoolean(Configuration.SPELL_IGNOREMIXEDCASE));
    }

    @Test
    void testSetInteger() {
        config.setInteger(Configuration.COST_REMOVE_CHAR, 5);
        assertEquals(5, config.getInteger(Configuration.COST_REMOVE_CHAR));

        config.setInteger(Configuration.COST_INSERT_CHAR, 10);
        assertEquals(10, config.getInteger(Configuration.COST_INSERT_CHAR));
    }
}
