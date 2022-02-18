package com.github.siroshun09.translationloader.api.test.util;

import com.github.siroshun09.translationloader.api.util.ExtensionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class ExtensionUtilTest {

    @Test
    void testPath() {
        var fromNull = ExtensionUtil.getExtension((Path) null);
        Assertions.assertNotNull(fromNull);
        Assertions.assertTrue(fromNull.isEmpty());

        var path = Path.of("test.txt");
        var fromPath = ExtensionUtil.getExtension(path);
        Assertions.assertEquals("txt", fromPath);

        var pathWithoutExtension = Path.of("test");
        var fromPathWithoutExtension = ExtensionUtil.getExtension(pathWithoutExtension);
        Assertions.assertNotNull(fromPathWithoutExtension);
        Assertions.assertTrue(fromPathWithoutExtension.isEmpty());
    }

    @Test
    void testStringPath() {
        var fromNull = ExtensionUtil.getExtension((String) null);
        Assertions.assertNotNull(fromNull);
        Assertions.assertTrue(fromNull.isEmpty());

        var fromFilename = ExtensionUtil.getExtension("test.txt");
        Assertions.assertEquals("txt", fromFilename);

        var fromFilenameWithoutExtension = ExtensionUtil.getExtension("test");
        Assertions.assertNotNull(fromFilenameWithoutExtension);
        Assertions.assertTrue(fromFilenameWithoutExtension.isEmpty());
    }
}
