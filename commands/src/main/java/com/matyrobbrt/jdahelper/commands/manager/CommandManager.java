package com.matyrobbrt.jdahelper.commands.manager;

import com.matyrobbrt.jdahelper.commands.Command;
import com.matyrobbrt.jdahelper.commands.SlashCommand;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class CommandManager {
    @Nullable
    private final LocalizationFunction defaultLocalizationFunction;
    private final List<Command<?>> commands;
    private final EventListener listener = new Listener();

    CommandManager(@Nullable LocalizationFunction defaultLocalizationFunction, List<Command<?>> commands) {
        this.defaultLocalizationFunction = defaultLocalizationFunction;
        this.commands = commands;
    }

    public static CommandManagerBuilder builder() {
        return new CommandManagerBuilder();
    }

    public EventListener getListener() {
        return listener;
    }

    private final class Listener implements EventListener {
        private Map<String, SlashCommand> listeners = Map.of();
        private Map<String, Command<UserContextInteractionEvent>> userListeners = Map.of();
        private Map<String, Command<MessageContextInteractionEvent>> messageListeners = Map.of();

        @Override
        @SuppressWarnings("unchecked")
        public void onEvent(@NotNull GenericEvent gevent) {
            if (gevent instanceof ReadyEvent event) {
                final var update = event.getJDA().updateCommands();
                listeners = buildSlashCommands(
                        commands.stream()
                            .filter(cmd -> cmd.getType() == net.dv8tion.jda.api.interactions.commands.Command.Type.SLASH)
                            .map(SlashCommand.class::cast),
                        update
                );
                userListeners = buildContext(
                        commands.stream()
                                .filter(cmd -> cmd.getType() == net.dv8tion.jda.api.interactions.commands.Command.Type.USER)
                                .map(c -> (Command<UserContextInteractionEvent>) c),
                        update,
                        Commands::user
                );
                messageListeners = buildContext(
                        commands.stream()
                                .filter(cmd -> cmd.getType() == net.dv8tion.jda.api.interactions.commands.Command.Type.MESSAGE)
                                .map(c -> (Command<MessageContextInteractionEvent>) c),
                        update,
                        Commands::message
                );
                update.queue();
            } else if (gevent instanceof SlashCommandInteractionEvent event) {
                final SlashCommand command = listeners.get(event.getFullCommandName());
                if (command != null) {
                    command.onEvent(event);
                }
            } else if (gevent instanceof CommandAutoCompleteInteractionEvent event) {
                final SlashCommand command = listeners.get(event.getFullCommandName());
                if (command != null) {
                    command.onAutoComplete(event);
                }
            } else if (gevent instanceof MessageContextInteractionEvent event) {
                final Command<MessageContextInteractionEvent> command = messageListeners.get(event.getName());
                if (command != null) {
                    command.onEvent(event);
                }
            } else if (gevent instanceof UserContextInteractionEvent event) {
                final Command<UserContextInteractionEvent> command = userListeners.get(event.getName());
                if (command != null) {
                    command.onEvent(event);
                }
            }
        }

        public Map<String, SlashCommand> buildSlashCommands(Stream<SlashCommand> commands, CommandListUpdateAction action) {
            final Map<String, SlashCommand> cmds = new HashMap<>();
            final List<CommandData> commandData = new ArrayList<>();
            commands.forEach(command -> {
                final SlashCommandData data = buildSlash(command);
                cmds.put(command.getName().getFallback(), command);
                for (final SlashCommand child : command.getChildren()) {
                    final var childChildren = child.getChildren();
                    if (childChildren.isEmpty()) {
                        final String path = command.getName().getFallback() + " " + child.getName().getFallback();
                        checkChildConditions(command, child, path);
                        data.addSubcommands(buildSubcommand(child));
                        cmds.put(path, child);
                    } else {
                        final SubcommandGroupData group = new SubcommandGroupData(
                                child.getName().getFallback(), child.getDescription().getFallback()
                        );
                        group.setNameLocalizations(child.getName().getLocalizations());
                        group.setDescriptionLocalizations(child.getDescription().getLocalizations());
                        final List<SubcommandData> children = new ArrayList<>();
                        for (final SlashCommand sub : childChildren) {
                            final String path = command.getName().getFallback() + " " + child.getName().getFallback() + " " + sub.getName().getFallback();
                            checkChildConditions(command, sub, path);
                            children.add(buildSubcommand(sub));
                            cmds.put(path, sub);
                        }
                        group.addSubcommands(children);
                        data.addSubcommandGroups(group);
                    }
                }
                commandData.add(data);
            });
            action.addCommands(commandData);
            return cmds;
        }

        private void checkChildConditions(SlashCommand parent, SlashCommand child, String path) {
            if (child.getLocalizationFunction() != null) {
                throw new IllegalArgumentException("Command children cannot have their own localization functions! Command path: /" + path);
            } else if (child.getPermissions() != null) {
                throw new IllegalArgumentException("Command children cannot have their own permissions! Command path: /" + path);
            } else if (child.isNSFW() && !parent.isNSFW()) {
                throw new IllegalArgumentException("Command children cannot have different NSFW status! Command path: /" + path);
            } else if (child.isGuildOnly() && !parent.isGuildOnly()) {
                throw new IllegalArgumentException("Command children cannot have different guild-only status! Command path: /" + path);
            }
        }

        public <T extends GenericContextInteractionEvent<?>> Map<String, Command<T>> buildContext(Stream<Command<T>> commands, CommandListUpdateAction action, Function<String, CommandData> builder) {
            final Map<String, Command<T>> cmds = new HashMap<>();
            final List<CommandData> commandData = new ArrayList<>();
            commands.forEach(command -> {
                final CommandData data = builder.apply(command.getName().getFallback());
                applyTo(data, command);
                commandData.add(data);
                cmds.put(command.getName().getFallback(), command);
            });
            action.addCommands(commandData);
            return cmds;
        }

        private SubcommandData buildSubcommand(SlashCommand command) {
            final SubcommandData data = new SubcommandData(command.getName().getFallback(), command.getDescription().getFallback());
            data.setNameLocalizations(command.getName().getLocalizations());
            data.setDescriptionLocalizations(command.getDescription().getLocalizations());
            data.addOptions(command.getOptions());
            return data;
        }

        private SlashCommandData buildSlash(SlashCommand command) {
            final SlashCommandData commandData = Commands.slash(command.getName().getFallback(), command.getDescription().getFallback());
            applyTo(commandData, command);
            commandData.addOptions(command.getOptions())
                    .setDescriptionLocalizations(command.getDescription().getLocalizations());
            return commandData;
        }

        private void applyTo(CommandData data, Command<?> command) {
            data.setName(command.getName().getFallback())
                    .setNameLocalizations(command.getName().getLocalizations())
                    .setGuildOnly(command.isGuildOnly())
                    .setNSFW(command.isNSFW())
                    .setDefaultPermissions(Objects.requireNonNullElse(command.getPermissions(), DefaultMemberPermissions.ENABLED));
            final var locFunction = command.getLocalizationFunction() == null ? defaultLocalizationFunction : command.getLocalizationFunction();
            if (locFunction != null) {
                data.setLocalizationFunction(locFunction);
            }
        }
    }
}
