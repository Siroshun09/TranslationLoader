package com.github.siroshun09.translationloader.api.test.util;

import com.github.siroshun09.translationloader.api.util.LocaleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Locale;

class LocaleParserTest {

    @Test
    void testStringLocales() {
        Assertions.assertEquals(Locale.ENGLISH, LocaleParser.parse("en"));
        Assertions.assertEquals(Locale.JAPAN, LocaleParser.parse("ja_JP"));
        Assertions.assertEquals(Locale.JAPANESE, LocaleParser.parse("ja"));

        Assertions.assertNull(LocaleParser.parse(null));
        Assertions.assertNull(LocaleParser.parse(""));
    }

    @Test
    void testPath() {
        Assertions.assertEquals(Locale.ENGLISH, LocaleParser.fromFileName(Path.of("en.yml")));
        Assertions.assertEquals(Locale.JAPAN, LocaleParser.fromFileName(Path.of("ja_JP.yml")));
        Assertions.assertEquals(Locale.JAPANESE, LocaleParser.fromFileName(Path.of("ja.yml")));
        Assertions.assertNull(LocaleParser.fromFileName(Path.of(".")));
    }

    @Test
    void testLocales() {
        testLocale(Locale.ENGLISH);
        testLocale(Locale.JAPAN);
        testLocale(Locale.JAPANESE);
    }

    private void testLocale(Locale locale) {
        Assertions.assertEquals(locale, LocaleParser.parse(locale.toString()));
    }
}
