package com.matyrobbrt.jdahelper.components;

import java.util.List;
import java.util.UUID;

/**
 * A component is the core of the Components system. <br>
 * It has an ID, and represents an {@link net.dv8tion.jda.api.interactions.components.Component}.<br>
 * It is used for saving the arguments that an {@link net.dv8tion.jda.api.interactions.components.ItemComponent} needs for later use.
 */
public record Component(String featureId, UUID uuid, List<String> arguments, ComponentLifespan lifespan) {

    /**
     * Button IDs will be split on this string, and only the first one will be used as the component ID,
     * in order to allow other arguments in the button itself, or to allow multiple buttons
     * with the same component ID.
     */
    public static final String ID_SPLITTER = "//";

    /**
     * Creates a button ID with the specified {@code id} as the component ID, and
     * the other arguments being split from the component ID using the {@link #ID_SPLITTER}.
     *
     * @param id        the component id
     * @param arguments other arguments
     * @return a composed ID, with the arguments being split from the component ID using the {@link #ID_SPLITTER}
     */
    public static String createIdWithArguments(final String id, final Object... arguments) {
        StringBuilder actualId = new StringBuilder(id);
        for (final var arg : arguments) {
            actualId.append(ID_SPLITTER).append(arg);
        }
        return actualId.toString();
    }

    /**
     * Creates a button ID with the specified {@code id} as the component ID, and
     * the other arguments being split from the component ID using the {@link #ID_SPLITTER}.
     *
     * @param id        the component id
     * @param arguments other arguments
     * @return a composed ID, with the arguments being split from the component ID using the {@link #ID_SPLITTER}
     */
    public static String createIdWithArguments(final UUID id, final Object... arguments) {
        StringBuilder actualId = new StringBuilder(id.toString());
        for (final var arg : arguments) {
            actualId.append(ID_SPLITTER).append(arg);
        }
        return actualId.toString();
    }

    /**
     * Creates a component with a {@link ComponentLifespan#temporary() temporary lifespan}.
     *
     * @param featureId the ID of the feature
     * @param uuid      the ID of the component
     * @param arguments the arguments used by the component
     */
    public Component(String featureId, UUID uuid, List<String> arguments) {
        this(featureId, uuid, arguments, ComponentLifespan.temporary());
    }

}
