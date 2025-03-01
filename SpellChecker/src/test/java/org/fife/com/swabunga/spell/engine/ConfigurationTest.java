package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Configuration}.
 */
class ConfigurationTest {

    @Test
    void testGetConfiguration_zeroArg_defaultValue() {
        Configuration config = Configuration.getConfiguration();
        Assertions.assertNotNull(config);
    }

    @Test
    void testGetConfiguration_oneArg_errorTriggersDefaults() {
        Configuration config = Configuration.getConfiguration("not/there");
        Assertions.assertNotNull(config);
    }
}
