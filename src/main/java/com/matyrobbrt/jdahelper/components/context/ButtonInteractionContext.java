package com.matyrobbrt.jdahelper.components.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.util.List;
import java.util.UUID;

/**
 * Represents the interaction with a button.
 */
public interface ButtonInteractionContext extends ItemComponentInteractionContext<ButtonInteraction> {

    /**
     * Gets the button that triggered this event.
     *
     * @return the button
     */
    default Button getButton() {
        return getEvent().getButton();
    }

    class Impl extends ItemComponentInteractionContext.Impl<ButtonInteraction> implements ButtonInteractionContext {

        public Impl(ButtonInteraction event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments, itemComponentArgs);
        }
    }

}
