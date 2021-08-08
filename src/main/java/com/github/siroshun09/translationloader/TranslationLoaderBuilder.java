package com.github.siroshun09.translationloader;

import com.github.siroshun09.configapi.api.file.FileConfiguration;
import com.github.siroshun09.configapi.api.file.PropertiesConfiguration;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

/**
 * A builder of {@link TranslationLoader}.
 */
public final class TranslationLoaderBuilder {

    private Path filePath;
    private Locale locale;
    private TranslationRegistry registry;

    TranslationLoaderBuilder() {
    }

    /**
     * Sets the filepath.
     *
     * @param filepath the filepath to load
     * @return this builder
     */
    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull TranslationLoaderBuilder setFilePath(Path filepath) {
        this.filePath = filepath;
        return this;
    }

    /**
     * Sets the locale
     *
     * @param locale the locale
     * @return this builder
     */
    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull TranslationLoaderBuilder setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Sets the {@link TranslationRegistry}.
     *
     * @param registry the {@link TranslationRegistry}
     * @return this builder
     */
    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull TranslationLoaderBuilder setRegistry(TranslationRegistry registry) {
        this.registry = registry;
        return this;
    }

    /**
     * Creates and sets the {@link TranslationRegistry}.
     *
     * @param key the {@link Key} of the registry
     * @return this builder
     */
    @Contract(value = "_ -> this", mutates = "this")
    public @NotNull TranslationLoaderBuilder setRegistry(@NotNull Key key) {
        this.registry = TranslationRegistry.create(key);
        return this;
    }

    /**
     * Creates new {@link FileConfigurationLoader}.
     *
     * @param func the {@link FileConfiguration} creator
     * @return the new {@link FileConfigurationLoader}
     */
    @Contract("_ -> new")
    public @NotNull FileConfigurationLoader createFileConfigurationLoader(@NotNull Function<Path, FileConfiguration> func) {
        Objects.requireNonNull(filePath);
        Objects.requireNonNull(locale);
        Objects.requireNonNull(registry);

        var config = Objects.requireNonNull(func).apply(filePath);
        return new FileConfigurationLoader(config, locale, registry);
    }

    /**
     * Creates new {@link FileConfigurationLoader} that loads from yaml file.
     *
     * @return new {@link FileConfigurationLoader} that loads from yaml file
     */
    @Contract(" -> new")
    public @NotNull FileConfigurationLoader createYamlConfigurationLoader() {
        return createFileConfigurationLoader(YamlConfiguration::create);
    }

    /**
     * Creates new {@link FileConfigurationLoader} that loads from properties file.
     *
     * @return new {@link FileConfigurationLoader} that loads from properties file
     */
    @Contract(" -> new")
    public @NotNull FileConfigurationLoader createPropertiesConfigurationLoader() {
        return createFileConfigurationLoader(PropertiesConfiguration::create);
    }
}
