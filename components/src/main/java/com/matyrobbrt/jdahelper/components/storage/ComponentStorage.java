package com.matyrobbrt.jdahelper.components.storage;

import com.matyrobbrt.jdahelper.components.Component;
import com.matyrobbrt.jdahelper.components.ComponentLifespan;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A class used for saving {@link Component}s
 */
@ParametersAreNonnullByDefault
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
     * Gets a component from the database.
     *
     * @param id the ID of the component to get
     * @return if the component exists, an optional containing it, otherwise an {@link Optional#empty() empty optional}.
     */
    @NotNull
    Optional<Component> getComponent(final UUID id);

    /**
     * {@return all components in this database}
     */
    Collection<Component> getAllComponents();

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
     * Removes all {@link ComponentLifespan#isTemporary() temporary} and {@link ComponentLifespan#getExpiryTime() without an expiry time} components which were last used before the given {@link Instant}. <br>
     *
     * @param before the last moment when components could have been used in order to "survive" this operation
     */
    void removeTemporaryLastUsedBefore(final Instant before);

    /**
     * Removes all {@link ComponentLifespan#isTemporary() temporary} components which
     * {@link ComponentLifespan#getExpiryTime() expired} (in order for a component to expire it must have a non-null {@link ComponentLifespan#getExpiryTime() expiry time}).
     */
    void removeExpiredComponents();

    /**
     * Removes all components with a permanent lifespan.
     */
    void removePermanentComponents();

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
     *       feature    text      not null,
     *       id         text      not null,
     *       arguments  text      not null,
     *       lifespan   bigint    not null,
     *       last_used  timestamp not null,
     *       constraint pk_components primary key (feature, id)
     *     );
     *     }
     * </pre>
     */
    @NotNull
    static ComponentStorage sql(final Jdbi jdbi, final String tableName) {
        return new SqlComponentStorage(jdbi, tableName);
    }

    /**
     * Creates an {@link InMemoryComponentStorage}.
     *
     * @param backingMap the backing map of the storage
     * @return the component storage
     */
    @NotNull
    static ComponentStorage inMemory(Map<UUID, InMemoryComponentStorage.ComponentInstance> backingMap) {
        return new InMemoryComponentStorage(backingMap);
    }
}
