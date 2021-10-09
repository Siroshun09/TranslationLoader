package com.github.siroshun09.translationloader.directory;

import com.github.siroshun09.translationloader.TranslationLoader;
import com.github.siroshun09.translationloader.util.ExtensionUtil;
import com.github.siroshun09.translationloader.util.LocaleParser;
import com.github.siroshun09.translationloader.util.PathConsumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A class that loads messages from files in a directory.
 */
public class TranslationDirectory {

    private final Path directory;
    private final Supplier<TranslationRegistry> registrySupplier;

    private final Set<TranslationLoader> loadedLoaders = new HashSet<>();
    private TranslationRegistry registry;

    /**
     * The constructor of {@link TranslationDirectory}.
     *
     * @param directory        the directory to load
     * @param registrySupplier the supplier to create {@link TranslationRegistry}
     * @deprecated Use {@link #create(Path, Supplier)}
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    public TranslationDirectory(@NotNull Path directory,
                                @NotNull Supplier<TranslationRegistry> registrySupplier) {
        this.directory = directory;
        this.registrySupplier = registrySupplier;
        this.registry = registrySupplier.get();
    }

    /**
     * The constructor of {@link TranslationDirectory}.
     *
     * @param directory the directory to load
     * @param key       the key of {@link TranslationRegistry}
     * @deprecated Use {@link #create(Path, Key)}
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
    public TranslationDirectory(@NotNull Path directory, @NotNull Key key) {
        this(directory, () -> TranslationRegistry.create(key));
    }

    /**
     * Creates a new {@link TranslationDirectory}.
     *
     * @param directory        the directory to load
     * @param registrySupplier the supplier to create {@link TranslationRegistry}
     * @return new {@link TranslationDirectory}
     */
    @Contract("_, _ -> new")
    public static @NotNull TranslationDirectory create(@NotNull Path directory,
                                                       @NotNull Supplier<TranslationRegistry> registrySupplier) {
        return new TranslationDirectory(directory, registrySupplier);
    }

    /**
     * Creates a new {@link TranslationDirectory}.
     *
     * @param directory the directory to load
     * @param key       the key of {@link TranslationRegistry}
     * @return new {@link TranslationDirectory}
     */
    @Contract("_, _ -> new")
    public static @NotNull TranslationDirectory create(@NotNull Path directory, @NotNull Key key) {
        return create(directory, () -> TranslationRegistry.create(key));
    }

    public void createDirectoryIfNotExists() throws IOException {
        if (!Files.isDirectory(directory)) {
            Files.createDirectories(directory);
        }
    }

    /**
     * Creates the directory if not exists.
     * <p>
     * If the directory is created, the {@link PathConsumer} will be called.
     *
     * @param onDirectoryCreated {@link PathConsumer}, called when a directory is created.
     * @throws IOException if I/O error occurred
     */
    public void createDirectoryIfNotExists(@NotNull PathConsumer onDirectoryCreated) throws IOException {
        if (Files.isDirectory(directory)) {
            return;
        }

        Files.createDirectories(directory);
        onDirectoryCreated.accept(directory);
    }

    /**
     * Loads message files from directory.
     *
     * @throws IOException if I/O error occurred
     */
    public void load() throws IOException {
        if (!loadedLoaders.isEmpty()) {
            unload();
        }

        if (!Files.isDirectory(directory)) {
            return;
        }

        try (var list = Files.list(directory)) {
            list.filter(Files::isRegularFile)
                    .map(this::loadFile)
                    .filter(Objects::nonNull)
                    .filter(TranslationLoader::isLoaded)
                    .forEach(loadedLoaders::add);
        }

        loadedLoaders.forEach(TranslationLoader::register);
        GlobalTranslator.get().addSource(registry);
    }

    /**
     * Unloads messages from the {@link GlobalTranslator}.
     */
    public void unload() {
        GlobalTranslator.get().removeSource(registry);
        loadedLoaders.clear();
        registry = registrySupplier.get();
    }

    /**
     * Gets the directory path.
     *
     * @return the directory path
     */
    public @NotNull Path getDirectory() {
        return directory;
    }

    /**
     * Gets the {@link TranslationRegistry} to register messages
     *
     * @return the {@link TranslationRegistry}
     */
    public @NotNull TranslationRegistry getRegistry() {
        return registry;
    }

    /**
     * Gets the loaded {@link TranslationLoader}s.
     *
     * @return the loaded {@link TranslationLoader}s
     */
    public @NotNull @Unmodifiable Set<TranslationLoader> getLoadedTranslationLoaders() {
        return Set.copyOf(loadedLoaders);
    }

    /**
     * Loads messages from the file.
     *
     * @param file the file to load
     * @return the {@link TranslationLoader} of the file
     */
    protected @Nullable TranslationLoader loadFile(@NotNull Path file) {
        var locale = LocaleParser.fromFileName(file);

        if (locale == null) {
            return null;
        }

        var builder =
                TranslationLoader.newBuilder()
                        .setFilePath(file)
                        .setLocale(locale)
                        .setRegistry(registry);

        TranslationLoader loader;

        switch (ExtensionUtil.getExtension(file)) {
            case "yml":
            case "yaml":
                loader = builder.createYamlConfigurationLoader();
                break;
            case "properties":
                loader = builder.createPropertiesConfigurationLoader();
                break;
            default:
                return null;
        }

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return loader;
    }
}
