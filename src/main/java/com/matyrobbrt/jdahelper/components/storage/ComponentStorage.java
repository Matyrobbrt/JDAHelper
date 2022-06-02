package com.matyrobbrt.jdahelper.components.storage;

import com.matyrobbrt.jdahelper.components.Component;
import org.jdbi.v3.core.Jdbi;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A class used for saving {@link Component}s
 */
public interface ComponentStorage {

    /**
     * Inserts a component into the database.
     *
     * @param component the component to insert
     */
    void insertComponent(final Component component);

    /**
     * Removes a component from the database.
     *
     * @param id the ID of the component to remove
     */
    void removeComponent(final UUID id);

    /**
     * Removes all the components which were last used before the given {@link Instant}. <br>
     * {@link com.matyrobbrt.jdahelper.components.Component.Lifespan#PERMANENT Permanent} components
     * all not removed by this operation.
     *
     * @param before the last moment when components could have been used in order to "survive" this operation
     */
    void removeComponentsLastUsedBefore(final Instant before);

    /**
     * Gets a component from the database.
     *
     * @param id the ID of the component to get
     * @return if the component exists, an optional containing it, otherwise an {@link Optional#empty() empty optional}.
     */
    Optional<Component> getComponent(final UUID id);

    /**
     * Updates the arguments for a given component.
     *
     * @param id           the ID of the component whose arguments to update
     * @param newArguments the new arguments of the component
     */
    void updateArguments(final UUID id, final List<String> newArguments);

    /**
     * Sets the last usage time for a component.
     *
     * @param id       the ID of the component to update
     * @param lastUsed the last usage time of the component
     */
    void setLastUsed(final UUID id, final Instant lastUsed);

    /**
     * Creates a {@link SqlComponentStorage}.
     *
     * @param jdbi      the {@link Jdbi} instance to use for accessing the database
     * @param tableName the name of the table that will store components
     * @return the component storage
     * @apiNote The table holding the components needs to have 5 rows, whose names
     * are the first 5 constants in {@link SqlComponentStorage}. It is recommended
     * that a Flyway migration is used for creating the table.
     * An example table creation statement:
     * <pre>
     *     {@code
     *     create table components
     *     (
     *     feature   text    not null,
     *     id        text    not null,
     *     arguments text    not null,
     *     lifespan  text    not null,
     *     last_used  timestamp not null,
     *     constraint pk_components primary key (feature, id)
     *     );
     *     }
     * </pre>
     */
    static ComponentStorage sql(final Jdbi jdbi, final String tableName) {
        return new SqlComponentStorage(jdbi, tableName);
    }
}
