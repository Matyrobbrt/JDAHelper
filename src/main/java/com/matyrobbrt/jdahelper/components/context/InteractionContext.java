package com.matyrobbrt.jdahelper.components.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The base class for component interaction contexts.
 *
 * @param <T> the type of the interaction
 */
@SuppressWarnings("unused")
public interface InteractionContext<T extends Interaction> {

    /**
     * Gets the event that triggered the interaction.
     *
     * @return the event that triggered the interaction
     */
    @NotNull
    T getEvent();

    /**
     * Gets the {@link Guild} this interaction happened in. <br>
     * This is null in direct messages.
     *
     * @return the guild, otherwise {@code null}
     */
    @Nullable
    default Guild getGuild() {
        return getEvent().getGuild();
    }

    /**
     * Gets the {@link Member} who caused this interaction. <br>
     * This is null if the interaction is not from a guild.
     *
     * @return the member, otherwise {@code null}
     */
    @Nullable
    default Member getMember() {
        return getEvent().getMember();
    }

    /**
     * Gets the {@link User} who caused this interaction.
     *
     * @return the user
     */
    @NotNull
    default User getUser() {
        return getEvent().getUser();
    }

    /**
     * Gets the component manager which dispatched this context.
     *
     * @return the component manager
     */
    @NotNull
    ComponentManager getManager();

    /**
     * Gets the arguments from the database.
     *
     * @return the arguments
     */
    @NotNull
    List<String> getArguments();

    /**
     * Gets an argument.
     *
     * @param index    the index of the argument to get
     * @param resolver a function that resolves the argument, if present
     * @param <Z>      the type of the resolved argument
     * @return if an argument at the {@code index} exists, that argument resolved, otherwise {@code null}
     */
    @Nullable
    default <Z> Z getArgument(final int index, final Function<? super String, ? extends Z> resolver) {
        if (index >= getArguments().size()) {
            return null;
        } else {
            return resolver.apply(getArguments().get(index));
        }
    }

    /**
     * Gets an argument.
     *
     * @param index        the index of the argument to get
     * @param defaultValue the default value of the argument
     * @param resolver     a function that resolves the argument, if present
     * @param <Z>          the type of the resolved argument
     * @return if an argument at the {@code index} exists, that argument resolved, otherwise the {@code defaultValue}
     */
    @NotNull
    default <Z> Z getArgument(final int index, final Supplier<Z> defaultValue, final Function<? super String, ? extends Z> resolver) {
        if (index >= getArguments().size()) {
            return defaultValue.get();
        } else {
            return resolver.apply(getArguments().get(index));
        }
    }

    /**
     * Gets the {@link com.matyrobbrt.jdahelper.components.Component#uuid() ID} of the component.
     *
     * @return the ID of the component
     */
    @NotNull
    UUID getComponentId();

    /**
     * Updates the arguments of the {@link com.matyrobbrt.jdahelper.components.Component component} which is linked
     * triggered this interaction.
     *
     * @param newArguments the new arguments
     */
    default void updateArguments(@NotNull final List<String> newArguments) {
        getManager().getStorage().updateArguments(getComponentId(), newArguments);
    }

    /**
     * Updates an argument of the {@link com.matyrobbrt.jdahelper.components.Component component} which triggered this interaction.
     *
     * @param index    the index of the argument to update
     * @param argument the new argument
     */
    default void updateArgument(final int index, @NotNull final String argument) {
        final var list = new ArrayList<>(getArguments());
        list.set(index, argument);
        updateArguments(list);
    }

    /**
     * Deletes the {@link #getComponentId() component} and all its associated
     * data from the database.
     */
    default void deleteComponent() {
        getManager().getStorage().removeComponent(getComponentId());
    }

    @SuppressWarnings("ClassCanBeRecord")
    class Impl<T extends Interaction> implements InteractionContext<T> {
        private final T event;
        private final ComponentManager manager;
        private final UUID componentId;
        private final List<String> arguments;

        public Impl(T event, ComponentManager manager, UUID componentId, List<String> arguments) {
            this.event = event;
            this.manager = manager;
            this.componentId = componentId;
            this.arguments = arguments;
        }

        @Override
        public @NotNull T getEvent() {
            return event;
        }

        @Override
        public @NotNull ComponentManager getManager() {
            return manager;
        }

        @Override
        public @NotNull List<String> getArguments() {
            return arguments;
        }

        @Override
        public @NotNull UUID getComponentId() {
            return componentId;
        }
    }
}
