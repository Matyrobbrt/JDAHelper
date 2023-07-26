package com.matyrobbrt.jdahelper.commands;

import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

class LocalizableBuilder implements Localizable.Builder {
    private final String key;
    @Nullable
    private Map<DiscordLocale, String> localizations;

    LocalizableBuilder(String key) {
        this.key = key;
    }

    @NotNull
    @Override
    public String getFallback() {
        return key;
    }

    @Override
    public Map<DiscordLocale, String> getLocalizations() {
        return localizations == null ? Map.of() : localizations;
    }

    @Override
    public Builder addLocalization(DiscordLocale locale, String localization) {
        (this.localizations == null ? this.localizations = new HashMap<>() : this.localizations).put(locale, localization);
        return this;
    }

    @Override
    public Builder addLocalizations(Map<DiscordLocale, String> localizations) {
        (this.localizations == null ? this.localizations = new HashMap<>() : this.localizations).putAll(localizations);
        return this;
    }
}
