package com.matyrobbrt.jdahelper.components.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Represents an {@link InteractionContext} for {@link net.dv8tion.jda.api.interactions.components.ItemComponent item components}.
 *
 * @param <T> the type of the interaction
 */
@SuppressWarnings("unused")
public interface ItemComponentInteractionContext<T extends IReplyCallback> extends InteractionContext<T>, IReplyCallback {

    /**
     * Gets the arguments from the {@link net.dv8tion.jda.api.interactions.components.ItemComponent item components}'s id. Those arguments are split from the component id
     * using the {@link com.matyrobbrt.jdahelper.components.Component#ID_SPLITTER}.
     *
     * @return the arguments from the item component id.
     */
    @NotNull
    List<String> getItemComponentArguments();

    /**
     * Gets the {@link Guild} this interaction happened in. <br>
     * This is null in direct messages.
     *
     * @return the guild, otherwise {@code null}
     */
    @Nullable
    @Override
    default Guild getGuild() {
        return InteractionContext.super.getGuild();
    }

    /**
     * Gets the {@link Member} who caused this interaction. <br>
     * This is null if the interaction is not from a guild.
     *
     * @return the member, otherwise {@code null}
     */
    @Nullable
    @Override
    default Member getMember() {
        return InteractionContext.super.getMember();
    }

    /**
     * Gets the {@link User} who caused this interaction.
     *
     * @return the user
     */
    @Override
    default @NotNull User getUser() {
        return InteractionContext.super.getUser();
    }

    class Impl<T extends IReplyCallback> extends InteractionContext.Impl<T> implements ItemComponentInteractionContext<T> {
        private final List<String> itemComponentArgs;

        public Impl(T event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments);
            this.itemComponentArgs = itemComponentArgs;
        }

        @Override
        public @NotNull List<String> getItemComponentArguments() {
            return itemComponentArgs;
        }

        @NotNull
        @Override
        public ReplyCallbackAction deferReply() {
            return getEvent().deferReply();
        }

        @NotNull
        @Override
        public InteractionHook getHook() {
            return getEvent().getHook();
        }

        @Override
        public int getTypeRaw() {
            return getEvent().getTypeRaw();
        }

        @NotNull
        @Override
        public String getToken() {
            return getEvent().getToken();
        }

        @Nullable
        @Override
        public Channel getChannel() {
            return getEvent().getChannel();
        }

        @Override
        public boolean isAcknowledged() {
            return getEvent().isAcknowledged();
        }

        @NotNull
        @Override
        public DiscordLocale getUserLocale() {
            return getEvent().getUserLocale();
        }

        @NotNull
        @Override
        public JDA getJDA() {
            return getEvent().getJDA();
        }

        @Override
        public long getIdLong() {
            return getEvent().getIdLong();
        }
    }
}
