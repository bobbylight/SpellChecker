package org.fife.com.swabunga.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link VectorUtility}.
 */
public class VectorUtilityTest {

    @Test
    void testAddAll_allowDuplicates() {
        List<String> dest = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> src = Arrays.asList("b", "c", "d");
        List<String> result = VectorUtility.addAll(dest, src, true);
        assertEquals(Arrays.asList("a", "b", "b", "c", "d"), result);
    }

    @Test
    void testAddAll_noDuplicates() {
        List<String> dest = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> src = Arrays.asList("b", "c", "d");
        List<String> result = VectorUtility.addAll(dest, src, false);
        assertEquals(Arrays.asList("a", "b", "c", "d"), result);
    }

    @Test
    void testAddAll_defaultAllowDuplicates() {
        List<String> dest = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> src = Arrays.asList("b", "c", "d");
        List<String> result = VectorUtility.addAll(dest, src);
        assertEquals(Arrays.asList("a", "b", "b", "c", "d"), result);
    }
}
