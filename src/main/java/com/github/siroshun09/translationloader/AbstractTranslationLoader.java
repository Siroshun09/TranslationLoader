package com.github.siroshun09.translationloader;

import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An abstract implementation of {@link TranslationLoader}.
 */
public abstract class AbstractTranslationLoader implements TranslationLoader {

    private final Path path;
    private final Locale locale;
    private final TranslationRegistry registry;
    private final Map<String, String> messageMap = new HashMap<>();

    private boolean isLoaded = false;

    /**
     * The constructor of {@link AbstractTranslationLoader}.
     *
     * @param path     the filepath to load
     * @param locale   the locale of messages that this loader will load
     * @param registry the {@link TranslationRegistry} to register messages
     */
    protected AbstractTranslationLoader(@NotNull Path path, @NotNull Locale locale,
                                        @NotNull TranslationRegistry registry) {
        this.path = Objects.requireNonNull(path);
        this.locale = Objects.requireNonNull(locale);
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Set if the message was loaded successfully.
     *
     * @param isLoaded true if the message was loaded, false otherwise
     */
    protected void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    @Override
    public boolean register() {
        if (!isLoaded) {
            return false;
        }

        var messageFormatMap =
                messageMap.entrySet()
                        .stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        Map.Entry::getKey,
                                        entry -> new MessageFormat(entry.getValue())
                                )
                        );

        registry.registerAll(locale, messageFormatMap);

        return true;
    }

    @Override
    public @NotNull Path getFilePath() {
        return path;
    }

    @Override
    public @NotNull TranslationRegistry getRegistry() {
        return registry;
    }

    @Override
    public @NotNull Locale getLocale() {
        return locale;
    }

    @Override
    public @NotNull @Unmodifiable Map<String, String> getMessageMap() {
        return Collections.unmodifiableMap(messageMap);
    }

    /**
     * Gets the modifiable message map.
     *
     * @return the modifiable message map
     */
    protected @NotNull Map<String, String> getModifiableMessageMap() {
        return messageMap;
    }
}
