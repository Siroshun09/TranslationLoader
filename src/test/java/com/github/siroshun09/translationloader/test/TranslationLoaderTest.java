package com.github.siroshun09.translationloader.test;

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.MappedConfiguration;
import com.github.siroshun09.configapi.api.util.ResourceUtils;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.translationloader.ConfigurationLoader;
import com.github.siroshun09.translationloader.FileConfigurationLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TranslationLoaderTest {

    private static final Path YAML_PATH = Path.of("en.yml");

    private static final Map<String, String> EXPECTED_ORIGINAL_MESSAGE_MAP;

    private static final Configuration OTHER_CONFIGURATION;
    private static final Map<String, String> EXPECTED_MERGED_MESSAGE_MAP;

    static {
        var map = new HashMap<String, String>();

        map.put("sample-key", "1");
        map.put("example.text", "abc");
        map.put("example.integer", "100");
        map.put("example.decimal", "12.3");
        map.put("example.bool", "true");

        EXPECTED_ORIGINAL_MESSAGE_MAP = Map.copyOf(map);

        OTHER_CONFIGURATION = MappedConfiguration.create();

        OTHER_CONFIGURATION.set("missing-message", "something");
        map.put("missing-message", "something");

        OTHER_CONFIGURATION.set("example.missing", "something");
        map.put("example.missing", "something");

        EXPECTED_MERGED_MESSAGE_MAP = Map.copyOf(map);
    }

    @BeforeAll
    static void prepareFile() throws IOException {
        ResourceUtils.copyFromClassLoader(ClassLoaderUtils.getDefaultClassLoader(), "original.yml", YAML_PATH);
    }

    @Test
    void testLoadingAndMerging() throws IOException {
        var loader = FileConfigurationLoader.create(YamlConfiguration.create(YAML_PATH));

        Assertions.assertFalse(loader.isLoaded());

        loader.load();

        Assertions.assertTrue(loader.isLoaded());

        Assertions.assertEquals(Locale.ENGLISH, loader.getLocale());
        Assertions.assertEquals("1.0.0", loader.getVersion());
        Assertions.assertEquals(EXPECTED_ORIGINAL_MESSAGE_MAP, loader.getMessageMap());

        Assertions.assertFalse(loader.isModified());

        var other = ConfigurationLoader.create(Locale.ENGLISH, OTHER_CONFIGURATION);

        Assertions.assertFalse(other.isLoaded());

        other.load();

        Assertions.assertTrue(other.isLoaded());

        loader.merge(other);

        Assertions.assertTrue(loader.isModified());
        Assertions.assertFalse(other.isModified());
        Assertions.assertEquals(EXPECTED_MERGED_MESSAGE_MAP, loader.getMessageMap());

        loader.save();

        Assertions.assertFalse(other.isModified());
    }

    @AfterAll
    static void deleteFile() throws IOException {
        Files.deleteIfExists(YAML_PATH);
    }
}
