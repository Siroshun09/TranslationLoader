package com.github.siroshun09.translationloader.directory;

import com.github.siroshun09.translationloader.TranslationLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Locale;

/**
 * An interface to merge missing messages to translations.
 */
@FunctionalInterface
public interface TranslationLoaderCreator {

    /**
     * Creates {@link TranslationLoader} of the {@link Locale}.
     * <p>
     * The loader is used to compare with the merge target and add missing messages.
     * <p>
     * Returning {@link TranslationLoader} must be loaded.
     *
     * @param locale the locale of the translation
     * @return the {@link TranslationLoader} for the locale if it exists, or {@code null} if it does not.
     * @throws IOException if I/O error occurred
     */
    @Nullable TranslationLoader createLoader(@NotNull Locale locale) throws IOException;

}
