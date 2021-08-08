package com.github.siroshun09.translationloader;

import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

/**
 * An interface to load messages from the file or the directory.
 */
public interface TranslationLoader {

    /**
     * Creates the builder of {@link TranslationLoader}.
     *
     * @return new {@link TranslationLoaderBuilder}
     */
    static @NotNull TranslationLoaderBuilder newBuilder() {
        return new TranslationLoaderBuilder();
    }

    /**
     * Loads messages from the file or the directory.
     *
     * @throws IOException if I/O error occurred
     */
    void load() throws IOException;

    /**
     * Returns whether this loader has succeeded in loading messages from the file or the directory.
     *
     * @return true if successful, false otherwise
     */
    boolean isLoaded();

    /**
     * Registers to {@link TranslationRegistry}.
     * <p>
     * If  {@link TranslationLoader#isLoaded()} is false, this method will not register to registry.
     *
     * @return true if the registration was successful, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean register();

    /**
     * Gets the path of the message file or the directory.
     *
     * @return the path of the message file
     */
    @NotNull Path getFilePath();

    /**
     * Gets the {@link TranslationRegistry} to register messages
     *
     * @return the {@link TranslationRegistry}
     */
    @NotNull TranslationRegistry getRegistry();

    /**
     * Gets the locale of messages that this loader will load.
     *
     * @return the locale of messages
     */
    @NotNull Locale getLocale();

    /**
     * Gets the map of messages based on key-value.
     *
     * @return the message map
     */
    @NotNull @Unmodifiable Map<String, String> getMessageMap();
}
