package com.github.siroshun09.translationloader.directory;

import com.github.siroshun09.translationloader.merger.MessageMerger;
import com.github.siroshun09.translationloader.util.PathConsumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A builder class to create {@link TranslationDirectory}.
 */
public final class TranslationDirectoryBuilder {

    private Path directory;
    private Supplier<TranslationRegistry> registrySupplier;
    private PathConsumer onDirectoryCreated;
    private String version;
    private MessageMerger messageMerger;

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
        return setRegistrySupplier(() -> TranslationRegistry.create(key));
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
     * add the missing messages using {@link MessageMerger}.
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
     * Sets the {@link MessageMerger} to add the missing messages.
     *
     * @param messageMerger the {@link MessageMerger} to add the missing messages
     * @return this builder
     */
    @Contract("_ -> this")
    public @NotNull TranslationDirectoryBuilder setMessageMerger(@NotNull MessageMerger messageMerger) {
        this.messageMerger = messageMerger;
        return this;
    }

    /**
     * Creates a new {@link TranslationDirectory}.
     *
     * @return a new {@link TranslationDirectory}
     * @throws NullPointerException the directory is not set by {@link #setDirectory(Path)}
     * @throws NullPointerException the registry supplier is not set by {@link #setRegistrySupplier(Supplier)} or {@link #setKey(Key)}
     */
    @Contract(value = "-> new", pure = true)
    public @NotNull TranslationDirectory build() {
        Objects.requireNonNull(directory);
        Objects.requireNonNull(registrySupplier);
        return new TranslationDirectory(directory, registrySupplier, onDirectoryCreated, version, messageMerger);
    }
}
