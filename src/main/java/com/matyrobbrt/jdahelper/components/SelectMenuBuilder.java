package com.matyrobbrt.jdahelper.components;

import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record SelectMenuBuilder<T extends SelectMenu, B extends SelectMenu.Builder<T, B>>(ComponentListener listener, Creator<T, B> creator) {

    /**
     * Creates a select menu builder with a random component ID.
     *
     * @param lifespan the lifespan of the component
     * @param args     the component's arguments
     * @return the select menu builder
     */
    @NotNull
    public B createMenu(@NotNull Component.Lifespan lifespan, final String... args) {
        return createMenu(lifespan, Arrays.asList(args));
    }

    /**
     * Creates a select menu builder with a random component ID.
     *
     * @param lifespan the lifespan of the component
     * @param args     the component's arguments
     * @return the select menu builder
     */
    @NotNull
    public B createMenu(@NotNull Component.Lifespan lifespan, final List<String> args) {
        final var comp = new Component(listener.getName(), UUID.randomUUID(), args, lifespan);
        listener.insertComponent(comp);
        return creator.create(comp.uuid().toString());
    }

    /**
     * Creates a select menu builder with a random component ID, and the specified menu ID arguments.
     *
     * @param lifespan    the lifespan of the component
     * @param args        the component's arguments
     * @param idArguments the menu's ID arguments
     * @return the select menu builder
     */
    @NotNull
    public B createMenu(@NotNull Component.Lifespan lifespan, final List<String> args, final Object... idArguments) {
        final var comp = new Component(listener.getName(), UUID.randomUUID(), args, lifespan);
        listener.insertComponent(comp);
        return creator.create(Component.createIdWithArguments(comp.uuid(), idArguments));
    }

    public interface Creator<T extends SelectMenu, B extends SelectMenu.Builder<T, B>> {
        B create(String id);
    }
}
