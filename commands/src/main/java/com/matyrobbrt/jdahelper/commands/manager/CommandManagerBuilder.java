package com.matyrobbrt.jdahelper.commands.manager;

import com.matyrobbrt.jdahelper.commands.Command;
import com.matyrobbrt.jdahelper.commands.CommandLike;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManagerBuilder {
    @Nullable
    private LocalizationFunction defaultLocalizationFunction;
    private final List<Command<?>> commands = new ArrayList<>();

    CommandManagerBuilder() {}

    public CommandManagerBuilder setDefaultLocalizationFunction(@Nullable LocalizationFunction defaultLocalizationFunction) {
        this.defaultLocalizationFunction = defaultLocalizationFunction;
        return this;
    }

    public CommandManagerBuilder addCommand(CommandLike<?> command) {
        this.commands.add(command.asCommand());
        return this;
    }

    public CommandManagerBuilder addCommands(CommandLike<?>... commands) {
        for (CommandLike<?> command : commands) {
            this.commands.add(command.asCommand());
        }
        return this;
    }

    public CommandManagerBuilder addCommands(List<? extends CommandLike<?>> commands) {
        commands.forEach(l -> this.commands.add(l.asCommand()));
        return this;
    }

    public CommandManager build() {
        return new CommandManager(
                defaultLocalizationFunction, List.copyOf(commands)
        );
    }
}
