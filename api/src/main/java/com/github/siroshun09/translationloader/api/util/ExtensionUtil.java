package com.github.siroshun09.translationloader.api.util;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * A utility class to get file extension.
 */
public final class ExtensionUtil {

    private static final String EMPTY_STRING = "";
    private static final char DOT = '.';

    /**
     * Gets the extension of file.
     *
     * @param path the path to get
     * @return an extension or an empty string if it could not be obtained
     */
    public static @NotNull String getExtension(Path path) {
        if (path == null) {
            return EMPTY_STRING;
        }

        var filePath = path.getFileName();

        if (filePath == null) {
            return EMPTY_STRING;
        }

        return getExtension(filePath.toString());
    }

    /**
     * Gets the extension of filename.
     *
     * @param fileName the filename to get
     * @return an extension or an empty string if it could not be obtained
     */
    public static @NotNull String getExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return EMPTY_STRING;
        }

        var lastDot = fileName.lastIndexOf(DOT);

        if (lastDot != -1) {
            return fileName.substring(lastDot + 1);
        } else {
            return EMPTY_STRING;
        }
    }

    private ExtensionUtil() {
        throw new UnsupportedOperationException();
    }
}
