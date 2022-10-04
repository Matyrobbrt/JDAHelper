package com.matyrobbrt.jdahelper.components.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.matyrobbrt.jdahelper.components.Component;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericTypes;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.TimestampedConfig;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Type;
import java.sql.Types;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SqlComponentStorage implements ComponentStorage {

    public static final String FEATURE_ROW_NAME = "feature";
    public static final String ID_ROW_NAME = "id";
    public static final String ARGUMENTS_ROW_NAME = "arguments";
    public static final String LIFESPAN_ROW_NAME = "lifespan";
    public static final String LAST_USED_ROW_NAME = "last_used";

    private static final Gson GSON = new GsonBuilder().setLenient().disableHtmlEscaping().create();

    private final Jdbi jdbi;
    private final String tableName;

    SqlComponentStorage(final Jdbi jdbi, final String tableName) {
        this.jdbi = jdbi;
        this.tableName = tableName;

        // Install the SQL Objects and Guava plugins
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerArgument(new ListArgumentFactory());
        jdbi.registerRowMapper(Component.class, (rs, ctx) ->
                new Component(rs.getString(FEATURE_ROW_NAME),
                        UUID.fromString(rs.getString(ID_ROW_NAME)),
                        listFromString(rs.getString(ARGUMENTS_ROW_NAME)),
                        Component.Lifespan.valueOf(rs.getString(LIFESPAN_ROW_NAME))));
        // Set default timezone to UTC
        jdbi.getConfig(TimestampedConfig.class).setTimezone(ZoneOffset.UTC);
    }

    @Override
    public void insertComponent(Component component) {
        jdbi.useHandle(handle -> handle.createUpdate("insert into %s (%s, %s, %s, %s, %s) values (:feature, :id, :arguments, :lifespan, :last_used)".formatted(
                        tableName, FEATURE_ROW_NAME, ID_ROW_NAME, ARGUMENTS_ROW_NAME, LIFESPAN_ROW_NAME, LAST_USED_ROW_NAME
                ))
                .bind(FEATURE_ROW_NAME, component.featureId())
                .bind(ID_ROW_NAME, component.uuid())
                .bind(ARGUMENTS_ROW_NAME, component.arguments())
                .bind(LIFESPAN_ROW_NAME, component.lifespan().toString())
                .bind(LAST_USED_ROW_NAME, Instant.now())
                .execute());
    }

    @Override
    public @NotNull Optional<Component> getComponent(UUID id) {
        final var comp = jdbi.withHandle(handle -> handle.createQuery("select %s, %s, %s, %s from %s where %s = :id".formatted(
                        FEATURE_ROW_NAME, ID_ROW_NAME, ARGUMENTS_ROW_NAME, LIFESPAN_ROW_NAME, tableName, ID_ROW_NAME
                ))
                .bind("id", id.toString())
                .mapTo(Component.class)
                .findOne());
        comp.ifPresent(component -> setLastUsed(id, Instant.now()));
        return comp;
    }

    @Override
    public void updateArguments(UUID id, List<String> newArguments) {
        jdbi.useHandle(handle -> handle.createUpdate("update %s set %s = :args, %s = :last_used where %s = :id".formatted(
                        tableName, ARGUMENTS_ROW_NAME, LAST_USED_ROW_NAME, ID_ROW_NAME
                ))
                .bind("args", newArguments)
                .bind("id", id.toString())
                .bind("last_used", Instant.now())
                .execute());
    }

    @Override
    public void setLastUsed(UUID id, Instant lastUsed) {
        jdbi.useHandle(handle -> handle.createUpdate("update %s set %s = :last_used where %s = :id".formatted(
                        tableName, LAST_USED_ROW_NAME, ID_ROW_NAME
                ))
                .bind(LAST_USED_ROW_NAME, lastUsed)
                .bind("id", id.toString())
                .execute());
    }

    @Override
    public void removeComponent(UUID id) {
        jdbi.useHandle(handle -> handle.createUpdate("delete from %s where %s = :id".formatted(
                        tableName, ID_ROW_NAME
                ))
                .bind("id", id)
                .execute());
    }

    @Override
    public void removeComponentsLastUsedBefore(Instant before) {
        jdbi.useHandle(handle -> handle.createUpdate("delete from %s where %s <= :before and %s == :lifespan".formatted(
                        tableName, LAST_USED_ROW_NAME, LIFESPAN_ROW_NAME
                ))
                .bind("before", before)
                .bind("lifespan", Component.Lifespan.TEMPORARY.toString())
                .execute());
    }

    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    private static List<String> listFromString(final String string) {
        return GSON.fromJson(string, STRING_LIST_TYPE);
    }

    public static final class ListArgumentFactory implements ArgumentFactory {

        private static final ArgumentFactory.Preparable LIST = new ListArg(Types.JAVA_OBJECT);

        @SuppressWarnings("rawtypes")
        private static final class ListArg extends AbstractArgumentFactory<java.util.List> {

            ListArg(int sqlType) {
                super(sqlType);
            }

            @Override
            protected Argument build(java.util.List value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, GSON.toJson(value));
            }
        }

        @Override
        public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
            if (List.class.isAssignableFrom(GenericTypes.getErasedType(type))) {
                return LIST.build(type, value, config);
            }
            return Optional.empty();
        }
    }
}
