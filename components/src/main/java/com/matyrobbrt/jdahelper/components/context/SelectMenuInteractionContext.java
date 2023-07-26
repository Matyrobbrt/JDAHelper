package com.matyrobbrt.jdahelper.components.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;

import java.util.List;
import java.util.UUID;

/**
 * Represents the interaction with a select menu.
 */
public interface SelectMenuInteractionContext<A, S extends SelectMenu, T extends SelectMenuInteraction<A, S>> extends ItemComponentInteractionContext<T> {

    class Impl<A, S extends SelectMenu, T extends SelectMenuInteraction<A, S>> extends ItemComponentInteractionContext.Impl<T> implements SelectMenuInteractionContext<A, S, T> {

        public Impl(T event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments, itemComponentArgs);
        }
    }

}
