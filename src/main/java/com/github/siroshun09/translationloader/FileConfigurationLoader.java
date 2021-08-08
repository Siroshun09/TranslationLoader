package com.github.siroshun09.translationloader;

import com.github.siroshun09.configapi.api.Configuration;
import com.github.siroshun09.configapi.api.file.FileConfiguration;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

/**
 * An implementation of {@link TranslationLoader} that loads messages from {@link FileConfiguration}.
 */
public class FileConfigurationLoader extends AbstractTranslationLoader {

    private final FileConfiguration config;

    /**
     * The constructor of {@link FileConfigurationLoader}.
     *
     * @param config the {@link FileConfiguration} to import messages
     * @param locale   the locale of messages that this loader will load
     * @param registry the {@link TranslationRegistry} to register messages
     */
    FileConfigurationLoader(@NotNull FileConfiguration config, @NotNull Locale locale,
                                      @NotNull TranslationRegistry registry) {
        super(config.getPath(), locale, registry);
        this.config = config;
    }

    @Override
    public void load() throws IOException {
        setLoaded(false);

        getModifiableMessageMap().clear();

        config.load();

        importMessagesFromConfiguration(config, "");

        setLoaded(true);
    }

    private void importMessagesFromConfiguration(@NotNull Configuration config, @NotNull String keyPrefix) {
        for (var key : config.getKeyList()) {
            var section = config.getSection(key);

            if (section != null) {
                var newKeyPrefix = keyPrefix + key + Configuration.PATH_SEPARATOR;
                importMessagesFromConfiguration(section, newKeyPrefix);
                continue;
            }

            var object = config.get(key);

            if (object != null) {
                var currentKey = keyPrefix + key;
                var message = object instanceof String ? (String) object : object.toString();

                getModifiableMessageMap().put(currentKey, message);
            }
        }
    }
}
