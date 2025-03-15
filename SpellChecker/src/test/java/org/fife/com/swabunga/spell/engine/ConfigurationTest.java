package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Configuration}.
 */
class ConfigurationTest {

    private String origJazzyConfig;

    @BeforeEach
    void setUp() {
        origJazzyConfig = System.getProperty(Configuration.PROPERTY_CONFIG_OVERRIDE);
    }

    @AfterEach
    void tearDown() {
        if (origJazzyConfig != null) {
            System.setProperty(Configuration.PROPERTY_CONFIG_OVERRIDE, origJazzyConfig);
        } else {
            System.clearProperty(Configuration.PROPERTY_CONFIG_OVERRIDE);
        }
    }

    @Test
    void testGetConfiguration_zeroArg_null_defaultValue() {
        Configuration config = Configuration.getConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    void testGetConfiguration_zeroArg_fromSystemProperty_defined() {
        System.setProperty(Configuration.PROPERTY_CONFIG_OVERRIDE,
                "org.fife.com.swabunga.spell.engine.PropertyConfiguration");
        Configuration config = Configuration.getConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    void testGetConfiguration_zeroArg_fromSystemProperty_emptyString_triggersDefaults() {
        System.setProperty(Configuration.PROPERTY_CONFIG_OVERRIDE, "no.such.ClassConfiguration");
        Configuration config = Configuration.getConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    void testGetConfiguration_zeroArg_fromSystemProperty_notDefined_triggersDefaults() {
        System.setProperty(Configuration.PROPERTY_CONFIG_OVERRIDE, "");
        Configuration config = Configuration.getConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    void testGetConfiguration_oneArg_emptyString_defaultValue() {
        Configuration config = Configuration.getConfiguration("");
        Assertions.assertNotNull(config);
    }

    @Test
    void testGetConfiguration_oneArg_errorTriggersDefaults() {
        Configuration config = Configuration.getConfiguration("not/there");
        Assertions.assertNotNull(config);
    }
}
