package com.matyrobbrt.jdahelper.commands.builder;

import com.matyrobbrt.jdahelper.commands.Command;
import com.matyrobbrt.jdahelper.commands.CommandLike;
import com.matyrobbrt.jdahelper.commands.Localizable;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public sealed abstract class CommandBuilder<E extends GenericCommandInteractionEvent, C extends Command<E>, S extends CommandBuilder<E, C, S>>
    implements CommandLike<C>
    permits SlashCommandBuilder, ContextCommandBuilder
{
    protected boolean wasBuilt;
    protected boolean isNSFW, isGuildOnly;
    protected Localizable name;
    @Nullable
    protected DefaultMemberPermissions permissions;
    @Nullable
    protected LocalizationFunction localizationFunction;
    protected Consumer<E> consumer;

    public S setNSFW() {
        return setNSFW(true);
    }

    public S setNSFW(boolean isNSFW) {
        return modifySelf(s -> s.isNSFW = isNSFW);
    }

    public S setGuildOnly() {
        return setGuildOnly(true);
    }

    public S setGuildOnly(boolean guildOnly) {
        return modifySelf(s -> s.isGuildOnly = guildOnly);
    }

    public S setName(Localizable name) {
        return modifySelf(s -> s.name = name);
    }

    public S setPermissions(@Nullable DefaultMemberPermissions permissions) {
        return modifySelf(s -> s.permissions = permissions);
    }

    public S setLocalizationFunction(@Nullable LocalizationFunction localizationFunction) {
        return modifySelf(s -> s.localizationFunction = localizationFunction);
    }

    public S action(Consumer<E> consumer) {
        return modifySelf(s -> s.consumer = consumer);
    }

    @SuppressWarnings("unchecked")
    protected final S modifySelf(Consumer<S> self) {
        if (wasBuilt) {
            throw new IllegalArgumentException("Cannot modify built command!");
        }
        final S t = (S) this;
        self.accept(t);
        return t;
    }

    public abstract C build();

    @Override
    public final C asCommand() {
        return build();
    }

    protected abstract class BaseCommand implements Command<E> {

        @Override
        public boolean isGuildOnly() {
            return isGuildOnly;
        }

        @Override
        public boolean isNSFW() {
            return isNSFW;
        }

        @Override
        public Localizable getName() {
            return name;
        }

        @Nullable
        @Override
        public DefaultMemberPermissions getPermissions() {
            return permissions;
        }

        @Override
        public void onEvent(E event) {
            if (consumer != null) {
                consumer.accept(event);
            }
        }
    }
}
