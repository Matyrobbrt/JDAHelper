package com.matyrobbrt.jdahelper.components.storage.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.util.List;
import java.util.UUID;

/**
 * Represents the interaction with a button.
 */
public interface ButtonInteractionContext extends ItemComponentInteractionContext<ButtonInteraction> {

    class Impl extends ItemComponentInteractionContext.Impl<ButtonInteraction> implements ButtonInteractionContext {

        public Impl(ButtonInteraction event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments, itemComponentArgs);
        }
    }

}
