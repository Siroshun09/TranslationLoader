package com.github.siroshun09.translationloader.directory;

import com.github.siroshun09.translationloader.util.PathConsumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A builder class to create {@link TranslationDirectory}.
 */
public final class TranslationDirectoryBuilder {

    private Path directory;

    private Supplier<TranslationRegistry> registrySupplier;
    private Key key;
    private Locale defaultLocale;

    private PathConsumer onDirectoryCreated;
    private String version;
    private TranslationLoaderCreator translationLoaderCreator;

    TranslationDirectoryBuilder() {
    }

    /**
     * Sets the directory path.
     *
     * @param directory the directory path
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder setDirectory(@NotNull Path directory) {
        this.directory = directory;
        return this;
    }

    /**
     * Sets the {@link Supplier} of the {@link TranslationRegistry} to create translation registry.
     *
     * @param registrySupplier the {@link Supplier} of the {@link TranslationRegistry}
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder setRegistrySupplier(@NotNull Supplier<TranslationRegistry> registrySupplier) {
        this.registrySupplier = registrySupplier;
        return this;
    }

    /**
     * Sets the {@link Key} to create {@link TranslationRegistry}.
     *
     * @param key the {@link Key} to create {@link TranslationRegistry}
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder setKey(@NotNull Key key) {
        this.key = key;
        return this;
    }

    /**
     * Sets the {@link Locale} to set {@link TranslationRegistry#defaultLocale(Locale)}.
     *
     * @param locale the default locale that used in {@link TranslationRegistry#defaultLocale(Locale)}
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder setDefaultLocale(@NotNull Locale locale) {
        this.defaultLocale = locale;
        return this;
    }

    /**
     * Sets the {@link PathConsumer} that is called when the directory is created.
     *
     * @param onDirectoryCreated the {@link PathConsumer} that is called when the directory is created
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder onDirectoryCreated(@NotNull PathConsumer onDirectoryCreated) {
        this.onDirectoryCreated = onDirectoryCreated;
        return this;
    }

    /**
     * Sets the translation version.
     * <p>
     * If this version differs from the version of the loaded {@link com.github.siroshun09.translationloader.TranslationLoader},
     * add the missing messages using {@link TranslationLoaderCreator}.
     *
     * @param version the translation version
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder setVersion(@NotNull String version) {
        this.version = version;
        return this;
    }

    /**
     * Sets the {@link TranslationLoaderCreator} to add the missing messages.
     *
     * @param translationLoaderCreator the {@link TranslationLoaderCreator} to add the missing messages
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder setTranslationLoaderCreator(@NotNull TranslationLoaderCreator translationLoaderCreator) {
        this.translationLoaderCreator = translationLoaderCreator;
        return this;
    }

    /**
     * Creates a new {@link TranslationDirectory}.
     *
     * @return a new {@link TranslationDirectory}
     * @throws NullPointerException the directory is not set by {@link #setDirectory(Path)}
     * @throws NullPointerException the registry supplier is not set by {@link #setRegistrySupplier(Supplier)} or could not be created
     */
    @Contract(value = "-> new", pure = true)
    public @NotNull TranslationDirectory build() {
        Objects.requireNonNull(directory);

        if (registrySupplier == null) {
            Objects.requireNonNull(key);

            registrySupplier = () -> {
                var registry = TranslationRegistry.create(key);

                if (defaultLocale != null) {
                    registry.defaultLocale(defaultLocale);
                }

                return registry;
            };
        }

        Objects.requireNonNull(registrySupplier);
        return new TranslationDirectory(directory, registrySupplier, onDirectoryCreated, version, translationLoaderCreator);
    }
}
