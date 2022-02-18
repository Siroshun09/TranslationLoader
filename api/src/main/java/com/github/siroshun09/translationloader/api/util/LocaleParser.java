package com.github.siroshun09.translationloader.api.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Locale;

/**
 * A utility class that parse string to {@link Locale}.
 */
public final class LocaleParser {

    /**
     * Parses the {@link Locale} from the file name.
     * <p>
     * Example: en.yml or en.example.yml -&gt; {@link Locale#ENGLISH}
     *
     * @param path the path to parse
     * @return the locale if the parse was successful, null otherwise
     */
    @Contract("null -> null")
    public static @Nullable Locale fromFileName(Path path) {
        if (path == null) {
            return null;
        }

        var filePath = path.getFileName();

        if (filePath == null) {
            return null;
        }

        var fileName = filePath.toString().toCharArray();
        var builder = new StringBuilder();

        for (var c : fileName) {
            if (c != '.') {
                builder.append(c);
            } else {
                break;
            }
        }

        return LocaleParser.parse(builder.toString());
    }

    /**
     * Parses string to {@link Locale}.
     *
     * @param str the string to parse.
     * @return the locale if the parse was successful, null otherwise
     * @see Locale
     */
    @Contract("null -> null")
    public static @Nullable Locale parse(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        var segments = str.split("_", 3);
        int length = segments.length;

        if (length == 1) {
            return new Locale(segments[0]); // language
        }

        if (length == 2) {
            return new Locale(segments[0], segments[1]); // language + country
        }

        if (length == 3) {
            return new Locale(segments[0], segments[1], segments[2]); // language + country + variant
        }

        return null;
    }

    private LocaleParser() {
        throw new UnsupportedOperationException();
    }
}
