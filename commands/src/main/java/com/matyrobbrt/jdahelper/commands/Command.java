package com.matyrobbrt.jdahelper.commands;

import com.matyrobbrt.jdahelper.commands.builder.ContextCommandBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An interface representing a {@linkplain net.dv8tion.jda.api.interactions.commands.build.CommandData Discord command}.
 *
 * @param <T> the type of the event the command will receive
 * @see SlashCommand
 * @see com.matyrobbrt.jdahelper.commands.manager.CommandManager
 */
public interface Command<T extends GenericCommandInteractionEvent> extends CommandLike<Command<T>> {
    /**
     * {@return the {@link net.dv8tion.jda.api.interactions.commands.Command.Type type} of this command}
     */
    @Nonnull
    net.dv8tion.jda.api.interactions.commands.Command.Type getType();

    /**
     * {@return the name of this command}
     */
    Localizable getName();

    /**
     * {@return a custom localization function this command provides, or {@code null}}
     * <strong>Note:</strong> this method cannot be used in sub-commands.
     */
    @Nullable
    default LocalizationFunction getLocalizationFunction() {
        return null;
    }

    /**
     * {@return whether this command is guild-only}
     */
    default boolean isGuildOnly() {
        return false;
    }

    /**
     * {@return whether this command is NSFW}
     */
    default boolean isNSFW() {
        return false;
    }

    /**
     * Called when this command was used by an user.
     *
     * @param event the event
     */
    default void onEvent(T event) {

    }

    /**
     * {@return the default permissions of the command, or {@code null}}
     * <strong>Note:</strong> this method cannot be used in sub-commands.
     */
    @Nullable
    default DefaultMemberPermissions getPermissions() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default Command<T> asCommand() {
        return this;
    }

    /**
     * Creates a new builder for {@link net.dv8tion.jda.api.interactions.commands.Command.Type#MESSAGE message context menu}.
     *
     * @return the builder
     */
    static ContextCommandBuilder<Message, MessageContextInteractionEvent> messageBuilder() {
        return new ContextCommandBuilder<>(net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE);
    }

    /**
     * Creates a new builder for {@link net.dv8tion.jda.api.interactions.commands.Command.Type#USER user context menu}.
     *
     * @return the builder
     */
    static ContextCommandBuilder<User, UserContextInteractionEvent> userBuilder() {
        return new ContextCommandBuilder<>(net.dv8tion.jda.api.interactions.commands.Command.Type.USER);
    }
}
