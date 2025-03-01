package org.fife.com.swabunga.spell.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Unit tests for {@link GenericTransformator}.
 */
class GenericTransformatorTest {

    private static final String TEMP_FILE_PREFIX = "scUnitTests_genericTransformator";

    @Test
    void testConstructor_oneArg_file() throws IOException {
        File file = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
        file.deleteOnExit();
        Assertions.assertDoesNotThrow(() -> new GenericTransformator(file));
    }

    @Test
    void testConstructor_oneArg_Reader() throws IOException {
        File file = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
        file.deleteOnExit();
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            Assertions.assertDoesNotThrow(() -> new GenericTransformator(r));
        }
    }

    @Test
    void testConstructor_twoArg() throws IOException {
        File file = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
        file.deleteOnExit();
        Assertions.assertDoesNotThrow(() -> new GenericTransformator(file, "UTF-8"));
    }

    @Test
    void testTransform_noRules() throws IOException {
        File file = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
        file.deleteOnExit();
        GenericTransformator transformator = new GenericTransformator(file);
        String input = "wonderful";
        Assertions.assertEquals("WONDERFUL", transformator.transform(input));
    }

    @Test
    void testTransform_noRules_digits() throws IOException {
        File file = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
        file.deleteOnExit();
        GenericTransformator transformator = new GenericTransformator(file);
        String input = "foo0123456789bar";
        Assertions.assertEquals("FOO0000000000BAR", transformator.transform(input));
    }

    @Test
    void testTransform_rules() throws IOException {
        File file = File.createTempFile(TEMP_FILE_PREFIX, ".txt");
        file.deleteOnExit();
        Files.writeString(file.toPath(), "version 1\n" +
                "# Comments are ignored\n" +
                "collapse_result    1   # This comment is ignored\n" +
                "alphabet[abcdefghijklmnopqrstuvwxyz]\n" +
                "ENOUGH^$    *NF\n" +
                "SCH(EOU)-   SK\n" +
                "SC(IEY)-    SI\n" +
                "XXX         _\n"
        );

        GenericTransformator transformator = new GenericTransformator(file);
        Assertions.assertEquals("*NF", transformator.transform("enough"));
        Assertions.assertEquals("SKEDULE", transformator.transform("schedule"));
        Assertions.assertEquals("SIIENCE", transformator.transform("science"));
        Assertions.assertEquals("FOOBAR", transformator.transform("fooxxxbar"));
    }
}
