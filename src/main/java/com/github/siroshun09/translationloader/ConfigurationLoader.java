package com.github.siroshun09.translationloader;

import com.github.siroshun09.configapi.api.Configuration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

public class ConfigurationLoader extends AbstractTranslationLoader {

    @Contract("_, _ -> new")
    public static @NotNull ConfigurationLoader create(@NotNull Locale locale, @NotNull Configuration source) {
        return new ConfigurationLoader(locale, source);
    }

    private final Configuration source;

    protected ConfigurationLoader(@NotNull Locale locale, @NotNull Configuration source) {
        super(locale);
        this.source = source;
    }

    @Override
    public void load() throws IOException {
        setLoaded(false);

        getModifiableMessageMap().clear();

        importMessagesFromConfiguration(source, "");
        setLoadedVersion(source.getString("v"));

        setLoaded(true);
    }

    @Override
    public void save() throws IOException {
        if (isModified()) {
            getMessageMap().forEach(source::set);
            source.set("v", getVersion());
            setModified(false);
        }
    }

    private void importMessagesFromConfiguration(@NotNull Configuration config, @NotNull String keyPrefix) {
        for (var key : config.getKeyList()) {
            if (key.equals("v") && keyPrefix.isEmpty()) {
                continue;
            }

            var section = config.getSection(key);

            if (section != null) {
                var newKeyPrefix = keyPrefix + key + Configuration.PATH_SEPARATOR;
                importMessagesFromConfiguration(section, newKeyPrefix);
                continue;
            }

            var object = config.get(key);
            var currentKey = keyPrefix + key;

            if (object != null) {
                var message = object instanceof String ? (String) object : object.toString();

                getModifiableMessageMap().put(currentKey, message);
            }
        }
    }
}
