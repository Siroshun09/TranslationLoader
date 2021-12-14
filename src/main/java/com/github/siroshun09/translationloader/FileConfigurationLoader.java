package com.github.siroshun09.translationloader;

import com.github.siroshun09.configapi.api.file.FileConfiguration;
import com.github.siroshun09.translationloader.util.LocaleParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

public class FileConfigurationLoader extends ConfigurationLoader {

    @Contract("_ -> new")
    public static @NotNull FileConfigurationLoader create(@NotNull FileConfiguration source) {
        var locale = LocaleParser.fromFileName(source.getPath());

        if (locale != null) {
            return create(locale, source);
        } else {
            throw new IllegalStateException("Could not get the locale");
        }
    }

    @Contract("_, _ -> new")
    public static @NotNull FileConfigurationLoader create(@NotNull Locale locale, @NotNull FileConfiguration source) {
        return new FileConfigurationLoader(locale, source);
    }

    private final FileConfiguration source;

    private FileConfigurationLoader(@NotNull Locale locale, @NotNull FileConfiguration source) {
        super(locale, source);
        this.source = source;
    }

    @Override
    public void load() throws IOException {
        setLoaded(false);

        try (source) {
            source.load();
            super.load();
        }
    }

    @Override
    public void save() throws IOException {
        if (!isModified()) {
            return;
        }

        try (source) {
            super.save();
            source.save();
        }
    }
}
