package com.github.siroshun09.translationloader.api;

import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * An interface that loads messages from the file.
 */
public interface TranslationLoader {

    /**
     * Loads messages from the file.
     *
     * @throws IOException if I/O error occurred
     */
    void load() throws IOException;

    /**
     * Returns whether this loader has succeeded in loading messages from the file.
     *
     * @return true if successful, false otherwise
     */
    boolean isLoaded();

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
    @NotNull @UnmodifiableView Map<String, String> getMessageMap();

    /**
     * Gets the version of the translation.
     * <p>
     * If this loader is not loaded, this method will return an empty string.
     *
     * @return the version of the translation
     */
    @NotNull String getVersion();

    /**
     * Registers messages to the {@link TranslationRegistry}.
     *
     * @param registry the registry to register messages
     * @return {@code true} if the registration was successful, {@code false} otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean register(@NotNull TranslationRegistry registry);
}
