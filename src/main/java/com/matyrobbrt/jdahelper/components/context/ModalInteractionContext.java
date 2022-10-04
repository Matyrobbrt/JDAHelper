package com.matyrobbrt.jdahelper.components.context;

import com.matyrobbrt.jdahelper.components.ComponentManager;
import net.dv8tion.jda.api.interactions.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Represents the interaction with a modal.
 */
public interface ModalInteractionContext extends ItemComponentInteractionContext<ModalInteraction> {

    /**
     * Convenience method to get a {@link net.dv8tion.jda.api.interactions.modals.ModalMapping ModalMapping} by its id from the List of {@link net.dv8tion.jda.api.interactions.modals.ModalMapping ModalMappings}
     *
     * <p>Returns null if no component with that id has been found
     *
     * @param id the custom id
     * @return ModalMapping with this id, or null if not found
     * @throws IllegalArgumentException if the provided id is null
     */
    @Nullable
    default ModalMapping getValue(@Nonnull String id) {
        return getEvent().getValue(id);
    }

    class Impl extends ItemComponentInteractionContext.Impl<ModalInteraction> implements ModalInteractionContext {

        public Impl(ModalInteraction event, ComponentManager manager, UUID componentId, List<String> arguments, List<String> itemComponentArgs) {
            super(event, manager, componentId, arguments, itemComponentArgs);
        }
    }

}