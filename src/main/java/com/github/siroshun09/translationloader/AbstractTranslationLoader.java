package com.github.siroshun09.translationloader;

import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class AbstractTranslationLoader implements TranslationLoader {

    private final Locale locale;
    private final Map<String, String> messageMap = new HashMap<>();

    private String version;
    private boolean isLoaded = false;
    private boolean isModified = false;

    /**
     * The constructor of {@link AbstractTranslationLoader}.
     *
     * @param locale the locale of messages that this loader will load
     */
    protected AbstractTranslationLoader(@NotNull Locale locale) {
        this.locale = Objects.requireNonNull(locale);
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean register(@NotNull TranslationRegistry registry) {
        if (isLoaded || isModified) {
            Collector<Map.Entry<String, String>, ?, Map<String, MessageFormat>> collector =
                    Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> new MessageFormat(entry.getValue()));

            registry.registerAll(locale, messageMap.entrySet().stream().collect(collector));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @NotNull Locale getLocale() {
        return locale;
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, String> getMessageMap() {
        return Collections.unmodifiableMap(messageMap);
    }

    @Override
    public @NotNull String getVersion() {
        return version != null ? version : "";
    }

    @Override
    public void setVersion(@NotNull String version) {
        Objects.requireNonNull(version);
        if (!this.version.equals(version)) {
            this.version = version;
            setModified(true);
        }
    }

    @Override
    public void merge(@NotNull TranslationLoader other) {
        if (!other.isLoaded()) {
            throw new IllegalStateException("The another TranslationLoader is not loaded.");
        }

        var map = other.getMessageMap();

        if (map.isEmpty()) {
            return;
        }

        for (var entry : map.entrySet()) {
            var key = Objects.requireNonNull(entry.getKey());
            var value = Objects.requireNonNull(entry.getValue());

            if (!messageMap.containsKey(key)) {
                messageMap.put(key, value);
                setModified(true);
            }
        }
    }

    @Override
    public boolean isModified() {
        return isModified;
    }

    /**
     * Gets the modifiable message map.
     *
     * @return the modifiable message map
     */
    protected @NotNull Map<String, String> getModifiableMessageMap() {
        return messageMap;
    }

    /**
     * Sets if the message was loaded successfully.
     *
     * @param isLoaded {@code true} if the message was loaded, {@code false} otherwise
     */
    protected void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Set if the message was loaded successfully.
     *
     * @param isModified {@code true} if this loader is modified, {@code false} otherwise
     */
    protected void setModified(boolean isModified) {
        this.isModified = isModified;
    }

    /**
     * Sets the version that loaded from the file.
     *
     * @param version the version
     */
    protected void setLoadedVersion(@NotNull String version) {
        this.version = version;
    }
}
