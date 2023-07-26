package com.matyrobbrt.jdahelper.commands.builder;

import com.matyrobbrt.jdahelper.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public final class ContextCommandBuilder<T, E extends GenericContextInteractionEvent<T>> extends CommandBuilder<E, com.matyrobbrt.jdahelper.commands.Command<E>, ContextCommandBuilder<T, E>> {
    private final net.dv8tion.jda.api.interactions.commands.Command.Type type;

    public ContextCommandBuilder(net.dv8tion.jda.api.interactions.commands.Command.Type type) {
        this.type = type;
    }

    @Override
    public Command<E> build() {
        this.wasBuilt = true;
        return new BaseCommand() {
            @NotNull
            @Override
            public net.dv8tion.jda.api.interactions.commands.Command.Type getType() {
                return type;
            }
        };
    }
}
