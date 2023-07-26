package com.matyrobbrt.jdahelper.commands.builder;

import com.matyrobbrt.jdahelper.commands.CommandLike;
import com.matyrobbrt.jdahelper.commands.Localizable;
import com.matyrobbrt.jdahelper.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class SlashCommandBuilder extends CommandBuilder<SlashCommandInteractionEvent, SlashCommand, SlashCommandBuilder> {
    private Localizable description = Localizable.of("No help available");
    private final List<OptionData> options = new ArrayList<>();
    private final List<SlashCommand> children = new ArrayList<>();
    private Consumer<CommandAutoCompleteInteractionEvent> onAutoComplete;

    public SlashCommandBuilder setDescription(Localizable description) {
        return modifySelf(s -> s.description = description);
    }

    public SlashCommandBuilder addOptions(OptionData... options) {
        return modifySelf(s -> s.options.addAll(Arrays.asList(options)));
    }

    public SlashCommandBuilder addChildren(CommandLike<SlashCommand> child) {
        return modifySelf(s -> s.children.add(child.asCommand()));
    }

    public SlashCommandBuilder onAutoComplete(Consumer<CommandAutoCompleteInteractionEvent> consumer) {
        return modifySelf(s -> s.onAutoComplete = consumer);
    }

    @Override
    public SlashCommand build() {
        this.wasBuilt = true;
        return new Cmd();
    }

    protected final class Cmd extends BaseCommand implements SlashCommand {

        @Override
        public Localizable getDescription() {
            return description;
        }

        @Override
        public List<OptionData> getOptions() {
            return options;
        }

        @Override
        public List<SlashCommand> getChildren() {
            return children;
        }

        @NotNull
        @Override
        public Command.Type getType() {
            return Command.Type.SLASH;
        }

        @Override
        public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
            if (onAutoComplete != null) {
                onAutoComplete.accept(event);
            }
        }
    }
}
