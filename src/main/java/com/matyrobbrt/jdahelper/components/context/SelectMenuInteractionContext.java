package com.matyrobbrt.jdahelper.components.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;

import java.util.List;
import java.util.UUID;

/**
 * Represents the interaction with a select menu.
 */
public interface SelectMenuInteractionContext extends ItemComponentInteractionContext<SelectMenuInteraction> {

    class Impl extends ItemComponentInteractionContext.Impl<SelectMenuInteraction> implements SelectMenuInteractionContext {

        public Impl(SelectMenuInteraction event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments, itemComponentArgs);
        }
    }

}
