package com.matyrobbrt.jdahelper.commands;

import com.matyrobbrt.jdahelper.commands.builder.SlashCommandBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SlashCommand extends Command<SlashCommandInteractionEvent> {
    Localizable getDescription();

    default List<SlashCommand> getChildren() {
        return List.of();
    }

    default List<OptionData> getOptions() {
        return List.of();
    }

    default void onAutoComplete(CommandAutoCompleteInteractionEvent event) {

    }

    @NotNull
    @Override
    default net.dv8tion.jda.api.interactions.commands.Command.Type getType() {
        return net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH;
    }

    static SlashCommandBuilder builder() {
        return new SlashCommandBuilder();
    }
}
