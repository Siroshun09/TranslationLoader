package com.github.siroshun09.translationloader.directory;

import com.github.siroshun09.configapi.api.file.PropertiesConfiguration;
import com.github.siroshun09.configapi.yaml.YamlConfiguration;
import com.github.siroshun09.translationloader.FileConfigurationLoader;
import com.github.siroshun09.translationloader.TranslationLoader;
import com.github.siroshun09.translationloader.merger.MessageMerger;
import com.github.siroshun09.translationloader.util.ExtensionUtil;
import com.github.siroshun09.translationloader.util.LocaleParser;
import com.github.siroshun09.translationloader.util.PathConsumer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A class that loads messages from files in a directory.
 */
public class TranslationDirectory {

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
        return new TranslationDirectory(directory, registrySupplier, null, null, null);
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

    /**
     * Creates a new {@link TranslationDirectoryBuilder}.
     *
     * @return new {@link TranslationDirectoryBuilder}
     */
    @Contract(value = " -> new", pure = true)
    public static @NotNull TranslationDirectoryBuilder newBuilder() {
        return new TranslationDirectoryBuilder();
    }

    private final Path directory;
    private final Supplier<TranslationRegistry> registrySupplier;
    private final @Nullable PathConsumer onDirectoryCreated;
    private final @Nullable String version;
    private final @Nullable MessageMerger messageMerger;

    private final Set<Locale> loadedLocales = new HashSet<>();

    private TranslationRegistry registry;

    TranslationDirectory(@NotNull Path directory, @NotNull Supplier<TranslationRegistry> registrySupplier,
                         @Nullable PathConsumer onDirectoryCreated,
                         @Nullable String version, @Nullable MessageMerger messageMerger) {
        this.directory = directory;
        this.registrySupplier = registrySupplier;
        this.onDirectoryCreated = onDirectoryCreated;
        this.version = version;
        this.messageMerger = messageMerger;
    }

    /**
     * Loads message files from directory.
     * <p>
     * If the directory does not exist, this method will create it.
     * <p>
     * This method may also use {@link MessageMerger} to add messages and save them to a file.
     *
     * @throws IOException if I/O error occurred
     */
    public void load() throws IOException {
        if (registry != null) {
            unload();
        }

        registry = registrySupplier.get();

        createDirectoryIfNotExists();

        try (var list = Files.list(directory)) {
            list.filter(Files::isRegularFile)
                    .map(this::loadFile)
                    .filter(Objects::nonNull)
                    .filter(TranslationLoader::isLoaded)
                    .forEach(this::updateAndRegister);
        }

        GlobalTranslator.get().addSource(registry);
    }

    /**
     * Unloads messages from the {@link GlobalTranslator}.
     */
    public void unload() {
        GlobalTranslator.get().removeSource(registry);
        loadedLocales.clear();
        registry = null;
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
        if (registry == null) {
            throw new IllegalStateException("The registry is not created (Not loaded yet?)");
        }

        return registry;
    }

    /**
     * Gets the set of the loaded {@link Locale}s.
     *
     * @return the set of the loaded {@link Locale}s
     */
    public Set<Locale> getLoadedLocales() {
        return loadedLocales;
    }

    private void createDirectoryIfNotExists() throws IOException {
        if (!Files.isDirectory(directory)) {
            Files.createDirectories(directory);

            if (onDirectoryCreated != null) {
                onDirectoryCreated.accept(directory);
            }
        }
    }

    private @Nullable TranslationLoader loadFile(@NotNull Path file) {
        var locale = LocaleParser.fromFileName(file);

        if (locale == null) {
            return null;
        }

        TranslationLoader loader;

        switch (ExtensionUtil.getExtension(file)) {
            case "yml":
            case "yaml":
                loader = FileConfigurationLoader.create(locale, YamlConfiguration.create(file));
                break;
            case "properties":
                loader = FileConfigurationLoader.create(locale, PropertiesConfiguration.create(file));
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

    private void updateAndRegister(@NotNull TranslationLoader loader) {
        if (messageMerger != null &&
                version != null && !version.isEmpty() && !loader.getVersion().equals(version)) {
            TranslationLoader other;

            try {
                other = messageMerger.createLoader(loader.getLocale());
            } catch (IOException e) {
                throw new RuntimeException("Could not get the merger (" + loader.getLocale() + ")", e);
            }

            if (other != null && other.isLoaded()) {
                loader.merge(other);
                loader.setVersion(other.getVersion());

                try {
                    loader.save();
                } catch (IOException e) {
                    throw new RuntimeException("Could not save the loader", e);
                }
            }
        }

        loader.register(registry);
        loadedLocales.add(loader.getLocale());
    }
}
