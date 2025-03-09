package org.fife.com.swabunga.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ListUtil}.
 */
class ListUtilTest {

    @Test
    void testAddAll() {
        List<String> dest = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> src = Arrays.asList("b", "c", "d");
        List<String> result = ListUtil.addAllNoDuplicates(dest, src);
        assertEquals(Arrays.asList("a", "b", "c", "d"), result);
    }
}
