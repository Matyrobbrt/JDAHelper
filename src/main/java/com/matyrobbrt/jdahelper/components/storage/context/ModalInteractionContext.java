package com.matyrobbrt.jdahelper.components.storage.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.interactions.ModalInteraction;

import java.util.List;
import java.util.UUID;

/**
 * Represents the interaction with a modal.
 */
public interface ModalInteractionContext extends ItemComponentInteractionContext<ModalInteraction> {

    class Impl extends ItemComponentInteractionContext.Impl<ModalInteraction> implements ModalInteractionContext {

        public Impl(ModalInteraction event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments, itemComponentArgs);
        }
    }

}