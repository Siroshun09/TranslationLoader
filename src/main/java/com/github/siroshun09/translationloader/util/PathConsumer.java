package com.github.siroshun09.translationloader.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A consumer of {@link Path}.
 */
@FunctionalInterface
public interface PathConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param path the path
     * @throws IOException if I/O error occurred
     */
    void accept(@NotNull Path path) throws IOException;
}
