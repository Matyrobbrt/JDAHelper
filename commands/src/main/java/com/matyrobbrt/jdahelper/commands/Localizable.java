package com.matyrobbrt.jdahelper.commands;

import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Localizable {
    @NotNull
    String getFallback();

    Map<DiscordLocale, String> getLocalizations();

    static Builder of(String fallback) {
        return new LocalizableBuilder(fallback);
    }

    interface Builder extends Localizable {
        Builder addLocalization(DiscordLocale locale, String localization);
        Builder addLocalizations(Map<DiscordLocale, String> localizations);
    }
}
