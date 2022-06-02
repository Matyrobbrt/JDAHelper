package com.matyrobbrt.jdahelper.components;

import static com.matyrobbrt.jdahelper.components.Component.ID_SPLITTER;
import com.matyrobbrt.jdahelper.components.storage.ComponentStorage;
import com.matyrobbrt.jdahelper.components.storage.context.ButtonInteractionContext;
import com.matyrobbrt.jdahelper.components.storage.context.ModalInteractionContext;
import com.matyrobbrt.jdahelper.components.storage.context.SelectMenuInteractionContext;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A {@link ComponentManager} is responsible for tracking {@link Component Components} and dispatching events affecing them to
 * the correct listeners, based on the {@link Component#featureId() component feature ID}.
 */
public class ComponentManager implements EventListener {

    private final ComponentStorage storage;
    final Map<String, ComponentListener> listeners = new HashMap<>();

    public ComponentManager(final ComponentStorage storage, final List<ComponentListener> listeners) {
        this.storage = storage;
        listeners.forEach(this::addListener);
    }

    /**
     * @return the storage of this manager
     */
    public ComponentStorage getStorage() {
        return storage;
    }

    /**
     * Removes {@link Component.Lifespan#TEMPORARY temporary} components that are older than the time specified.
     *
     * @param time the time
     * @param unit the unit
     */
    public void removeComponentsOlderThan(final long time, final TemporalUnit unit) {
        getStorage().removeComponentsLastUsedBefore(Instant.now().minus(time, unit));
    }

    /**
     * Adds a listener to this manager.
     *
     * @param listener the listener
     */
    public void addListener(final ComponentListener listener) {
        final var id = listener.getName();
        if (listeners.containsKey(id)) {
            throw new IllegalArgumentException("Listener with feature ID \"" + id + "\" exists already!");
        }
        listener.setManager(this);
        listeners.put(id, listener);
    }

    @Override
    @SubscribeEvent
    public void onEvent(@NotNull final GenericEvent event) {
        try {
            if (event instanceof ButtonInteractionEvent btn) {
                onButtonInteraction(btn);
            } else if (event instanceof SelectMenuInteractionEvent sEvent) {
                onSelectMenuInteraction(sEvent);
            } else if (event instanceof ModalInteractionEvent mEvent) {
                onModalInteraction(mEvent);
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {
            // Usually caused by `String#split` or `UUID.fromString`
        }
    }

    private void onButtonInteraction(@NotNull final ButtonInteractionEvent event) {
        if (event.getButton().getId() != null) {
            final var buttonArguments = event.getButton().getId().split(ID_SPLITTER);
            final var id = UUID.fromString(buttonArguments[0]);
            getStorage().getComponent(id).ifPresentOrElse(component -> {
                final var listener = listeners.get(component.featureId());
                if (listener == null) {
                    event.deferReply(true).setContent("It seems like I can't handle this button anymore due to its listener being deleted.").queue();
                } else {
                    listener.onButtonInteraction(new ButtonInteractionContext.Impl(
                            event, this, component.uuid(),
                            component.arguments(), splitItemComponentArguments(buttonArguments)
                    ));
                }
            }, () -> replyWithUnknown(event, "button"));
        }
    }

    private void onSelectMenuInteraction(@NotNull final SelectMenuInteractionEvent event) {
        final var menuArgs = Objects.requireNonNull(event.getSelectMenu().getId()).split(ID_SPLITTER);
        final var id = UUID.fromString(menuArgs[0]);
        getStorage().getComponent(id).ifPresentOrElse(component -> {
            final var listener = listeners.get(component.featureId());
            if (listener == null) {
                event.deferReply(true).setContent("It seems like I can't handle this select menu anymore due to its listener being deleted.").queue();
            } else {
                listener.onSelectMenuInteraction(new SelectMenuInteractionContext.Impl(
                        event, this, component.uuid(),
                        component.arguments(), splitItemComponentArguments(menuArgs)
                ));
            }
        }, () -> replyWithUnknown(event, "select menu"));
    }

    private void onModalInteraction(@NotNull final ModalInteractionEvent event) {
        final var modalArgs = event.getModalId().split(ID_SPLITTER);
        final var id = UUID.fromString(modalArgs[0]);
        getStorage().getComponent(id).ifPresentOrElse(component -> {
            final var listener = listeners.get(component.featureId());
            if (listener == null) {
                event.deferReply(true).setContent("It seems like I can't handle this modal anymore due to its listener being deleted.").queue();
            } else {
                listener.onModalInteraction(new ModalInteractionContext.Impl(
                        event, this, component.uuid(),
                        component.arguments(), splitItemComponentArguments(modalArgs)
                ));
            }
        }, () -> replyWithUnknown(event, "modal"));
    }

    private static List<String> splitItemComponentArguments(String[] args) {
        return args.length == 1 ? List.of() : Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
    }

    public void replyWithUnknown(IReplyCallback event, String type) {
        event.deferReply(true)
                .setContent("I am sorry. It seems like I don't know what this " + type + " does anymore. <:sadge:926848859668353055>")
                .queue();
    }
}
