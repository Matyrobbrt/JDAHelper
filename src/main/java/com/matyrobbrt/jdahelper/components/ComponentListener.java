package com.matyrobbrt.jdahelper.components;

import com.matyrobbrt.jdahelper.components.storage.context.ButtonInteractionContext;
import com.matyrobbrt.jdahelper.components.storage.context.ModalInteractionContext;
import com.matyrobbrt.jdahelper.components.storage.context.SelectMenuInteractionContext;
import com.matyrobbrt.jdahelper.util.ButtonBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A listener for component interactions. <br>
 * Listeners need to be {@link ComponentManager#addListener(ComponentListener) registered} to a {@link ComponentManager manager}
 * in order to allow them to handle interactions.
 */
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public abstract class ComponentListener {
    private final String name;
    private ComponentManager manager;

    ComponentListener(final String name) {
        this.name = name;
    }

    /**
     * Creates a button with a random component ID.
     *
     * @param style    the style
     * @param label    the label
     * @param emoji    the emoji
     * @param lifespan the component lifespan
     * @param args     the component arguments
     * @return the button
     */
    @NotNull
    public Button createButton(@NotNull ButtonStyle style, @Nullable String label, @Nullable Emoji emoji, @NotNull Component.Lifespan lifespan, List<String> args) {
        final var comp = new Component(name, UUID.randomUUID(), args, lifespan);
        insertComponent(comp);
        return Button.of(style, comp.uuid().toString(), label, emoji);
    }

    /**
     * Creates a new {@link ButtonBuilder} with a random component ID.
     *
     * @param style    the button style
     * @param lifespan the component lifespan
     * @param args     the component arguments
     * @return the builder
     */
    @NotNull
    public ButtonBuilder createButton(ButtonStyle style, Component.Lifespan lifespan, List<String> args) {
        final var comp = new Component(name, UUID.randomUUID(), args, lifespan);
        insertComponent(comp);
        return ButtonBuilder.builder(style)
                .idOrUrl(comp.uuid().toString());
    }

    /**
     * Creates a new {@link ButtonBuilder} with a random component ID, and the specified button ID arguments.
     *
     * @param style       the button style
     * @param lifespan    the component lifespan
     * @param args        the component arguments
     * @param idArguments the button's ID arguments
     * @return the builder
     */
    @NotNull
    public ButtonBuilder createButton(ButtonStyle style, Component.Lifespan lifespan, List<String> args, Object... idArguments) {
        final var comp = new Component(name, UUID.randomUUID(), args, lifespan);
        insertComponent(comp);
        return ButtonBuilder.builder(style)
                .idOrUrl(Component.createIdWithArguments(comp.featureId(), idArguments));
    }

    /**
     * Creates a select menu builder with a random component ID.
     *
     * @param lifespan the lifespan of the component
     * @param args     the component's arguments
     * @return the select menu builder
     */
    @NotNull
    public SelectMenu.Builder createMenu(@NotNull Component.Lifespan lifespan, final String... args) {
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
    public SelectMenu.Builder createMenu(@NotNull Component.Lifespan lifespan, final List<String> args) {
        final var comp = new Component(name, UUID.randomUUID(), args, lifespan);
        insertComponent(comp);
        return SelectMenu.create(comp.uuid().toString());
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
    public SelectMenu.Builder createMenu(@NotNull Component.Lifespan lifespan, final List<String> args, final Object... idArguments) {
        final var comp = new Component(name, UUID.randomUUID(), args, lifespan);
        insertComponent(comp);
        return SelectMenu.create(Component.createIdWithArguments(comp.uuid(), idArguments));
    }

    /**
     * Creates a modal builder with a random component ID.
     *
     * @param label    the label of the modal
     * @param lifespan the lifespan of the component
     * @param args     the component's arguments
     * @return the modal builder
     */
    @NotNull
    public Modal.Builder createModal(@NotNull final String label, @NotNull final Component.Lifespan lifespan, final String... args) {
        return createModal(label, lifespan, Arrays.asList(args));
    }

    /**
     * Creates a modal builder with a random component ID.
     *
     * @param label    the label of the modal
     * @param lifespan the lifespan of the component
     * @param args     the component's arguments
     * @return the modal builder
     */
    @NotNull
    public Modal.Builder createModal(@NotNull final String label, @NotNull final Component.Lifespan lifespan, final List<String> args) {
        final var comp = new Component(name, UUID.randomUUID(), args, lifespan);
        insertComponent(comp);
        return Modal.create(comp.uuid().toString(), label);
    }

    /**
     * Creates a modal builder with a random component ID, and the specified modal ID arguments.
     *
     * @param label       the label of the modal
     * @param lifespan    the lifespan of the component
     * @param args        the component's arguments
     * @param idArguments the menu's ID arguments
     * @return the modal builder
     */
    @NotNull
    public Modal.Builder createModal(@NotNull final String label, @NotNull final Component.Lifespan lifespan, final List<String> args, final Object... idArguments) {
        final var comp = new Component(name, UUID.randomUUID(), args, lifespan);
        insertComponent(comp);
        return Modal.create(Component.createIdWithArguments(comp.uuid(), idArguments), label);
    }

    /**
     * Inserts a component into the database.
     *
     * @param component the component to insert
     */
    public void insertComponent(final Component component) {
        manager.getStorage().insertComponent(component);
    }

    /**
     * Inserts a component into the database.
     *
     * @param id       the component's ID
     * @param lifespan the component's lifespan
     * @param args     the component's arguments
     * @return the component
     */
    public Component insertComponent(final UUID id, final Component.Lifespan lifespan, final String... args) {
        final var comp = new Component(getName(), id, Arrays.asList(args), lifespan);
        insertComponent(comp);
        return comp;
    }

    void setManager(final ComponentManager manager) {
        this.manager = manager;
    }

    /**
     * Handles the interaction with a button.
     *
     * @param context the context
     */
    public abstract void onButtonInteraction(final ButtonInteractionContext context);

    /**
     * Handles the interaction with a select menu.
     *
     * @param context the context
     */
    public abstract void onSelectMenuInteraction(final SelectMenuInteractionContext context);

    /**
     * Handles the interaction with a modal.
     *
     * @param context the context
     */
    public abstract void onModalInteraction(final ModalInteractionContext context);

    /**
     * Creates a new listener builder.
     *
     * @param featureId the ID of the feature that the listener manages
     * @param whenBuilt an action that should be executed when the listener is built
     * @return the builder
     */
    public static Builder builder(@NotNull String featureId, @Nullable Consumer<? super ComponentListener> whenBuilt) {
        return new Builder(featureId, whenBuilt);
    }

    public static final class Builder {
        private final String name;
        @Nullable
        private final Consumer<? super ComponentListener> whenBuilt;
        private Consumer<? super ButtonInteractionContext> onButton;
        private Consumer<? super SelectMenuInteractionContext> onSelectMenu;
        private Consumer<? super ModalInteractionContext> onModal;

        Builder(final String name, @Nullable final Consumer<? super ComponentListener> whenBuilt) {
            this.name = name;
            this.whenBuilt = whenBuilt;
        }

        /**
         * Sets the action that should be executed on {@link Button} interaction.
         *
         * @param onButton the action that should be executed on button interaction
         * @return the builder instance
         */
        public Builder onButtonInteraction(final Consumer<? super ButtonInteractionContext> onButton) {
            this.onButton = onButton;
            return this;
        }

        /**
         * Sets the action that should be executed on {@link net.dv8tion.jda.api.interactions.components.selections.SelectMenu} interaction.
         *
         * @param onSelectMenu the action that should be executed on select menu interaction
         * @return the builder instance
         */
        public Builder onSelectMenuInteraction(final Consumer<? super SelectMenuInteractionContext> onSelectMenu) {
            this.onSelectMenu = onSelectMenu;
            return this;
        }

        /**
         * Sets the action that should be executed on {@link net.dv8tion.jda.api.interactions.components.Modal} interaction.
         *
         * @param onModal the action that should be executed on modal interaction
         * @return the builder instance
         */
        public Builder onModalInteraction(final Consumer<? super ModalInteractionContext> onModal) {
            this.onModal = onModal;
            return this;
        }

        /**
         * Builds the listener.
         *
         * @return the built listener
         */
        public ComponentListener build() {
            final Consumer<? super ButtonInteractionContext> onButton = this.onButton == null ? b -> {
            } : this.onButton;
            final Consumer<? super SelectMenuInteractionContext> onSelectMenu = this.onSelectMenu == null ? b -> {
            } : this.onSelectMenu;
            final Consumer<? super ModalInteractionContext> onModal = this.onModal == null ? b -> {
            } : this.onModal;
            final var lis = new ComponentListener(name) {
                @Override
                public void onButtonInteraction(final ButtonInteractionContext context) {
                    onButton.accept(context);
                }

                @Override
                public void onSelectMenuInteraction(final SelectMenuInteractionContext context) {
                    onSelectMenu.accept(context);
                }

                @Override
                public void onModalInteraction(final ModalInteractionContext context) {
                    onModal.accept(context);
                }
            };
            if (whenBuilt != null) {
                whenBuilt.accept(lis);
            }
            return lis;
        }
    }

    public String getName() {
        return name;
    }
}
