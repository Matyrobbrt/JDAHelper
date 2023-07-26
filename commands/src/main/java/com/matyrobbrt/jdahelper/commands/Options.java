package com.matyrobbrt.jdahelper.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * An utility class used to create {@link OptionData}s.
 */
public class Options {

    public static OptionData string(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.STRING, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData integer(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.INTEGER, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData bool(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.BOOLEAN, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData user(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.USER, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData channel(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.CHANNEL, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData role(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.ROLE, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData mentionable(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.MENTIONABLE, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData number(Localizable name, Localizable description) {
        return applyLocalization(new OptionData(
                OptionType.NUMBER, name.getFallback(), description.getFallback()
        ), name, description);
    }

    public static OptionData applyLocalization(OptionData optionData, Localizable name, Localizable description) {
        if (name.getLocalizations() != null) {
            optionData.setNameLocalizations(name.getLocalizations());
        }
        if (description.getLocalizations() != null) {
            optionData.setDescriptionLocalizations(description.getLocalizations());
        }
        return optionData;
    }
}
