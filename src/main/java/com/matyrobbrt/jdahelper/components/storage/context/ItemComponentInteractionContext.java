package com.matyrobbrt.jdahelper.components.storage.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Represents an {@link InteractionContext} for {@link net.dv8tion.jda.api.interactions.components.ItemComponent item components}.
 *
 * @param <T> the type of the interaction
 */
public interface ItemComponentInteractionContext<T extends Interaction> extends InteractionContext<T> {

    /**
     * Gets the arguments from the {@link net.dv8tion.jda.api.interactions.components.ItemComponent item components}'s id. Those arguments are split from the component id
     * using the {@link com.matyrobbrt.jdahelper.components.Component#ID_SPLITTER}.
     *
     * @return the arguments from the item component id.
     */
    @NotNull
    List<String> getItemComponentArguments();

    class Impl<T extends Interaction> extends InteractionContext.Impl<T> implements ItemComponentInteractionContext<T> {
        private final List<String> itemComponentArgs;

        public Impl(T event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments);
            this.itemComponentArgs = itemComponentArgs;
        }

        @Override
        public @NotNull List<String> getItemComponentArguments() {
            return itemComponentArgs;
        }
    }
}
