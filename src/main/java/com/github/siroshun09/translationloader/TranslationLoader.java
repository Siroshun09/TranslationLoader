package com.github.siroshun09.translationloader;

import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public interface TranslationLoader {

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
     * Registers messages to the {@link TranslationRegistry}.
     *
     * @param registry the registry to register messages
     * @return {@code true} if the registration was successful, {@code false} otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean register(@NotNull TranslationRegistry registry);

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
     * Sets the version of this translation.
     *
     * @param newVersion the new version
     */
    void setVersion(@NotNull String newVersion);

    /**
     * Merges the other {@link TranslationLoader} into this loader.
     * <p>
     * If there are message keys that does not exist in this loader,
     * they will be added from another loader.
     *
     * @param other the other {@link TranslationLoader}
     * @throws IllegalStateException the other {@link TranslationLoader} is not loaded
     */
    void merge(@NotNull TranslationLoader other);

    /**
     * Checks if this loader is modified by {@link #merge(TranslationLoader)} method.
     *
     * @return {@code true} if this loader is modified, {@code false} otherwise
     */
    boolean isModified();

    /**
     * Saves messages to the file if {@link #isModified()} returns {@code true}.
     *
     * @throws IOException if I/O error occurred
     */
    void save() throws IOException;
}
