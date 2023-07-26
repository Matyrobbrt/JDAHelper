package com.matyrobbrt.jdahelper.test.components;

import com.matyrobbrt.jdahelper.components.Component;
import com.matyrobbrt.jdahelper.components.ComponentLifespan;
import com.matyrobbrt.jdahelper.components.storage.ComponentStorage;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ComponentsStorageTest {
    public static final Supplier<List<Component>> EXAMPLES = () -> List.of(
            new Component("featureA", UUID.randomUUID(), Lists.newArrayList("arg1", null, "arg3"), ComponentLifespan.permanent()),
            new Component("featureB", UUID.randomUUID(), List.of("the arg 1"), ComponentLifespan.temporary()),
            new Component("featureC", UUID.randomUUID(), List.of(), ComponentLifespan.expiringIn(Duration.ofSeconds(3)))
    );

    @ParameterizedTest
    @ArgumentsSource(StorageArguments.class)
    void checkPermanentTimeDeletion(final ComponentStorage storage) throws InterruptedException {
        final var examples = EXAMPLES.get();
        examples.forEach(storage::insertComponent);
        while (System.currentTimeMillis() < examples.get(2).lifespan().getExpiryTime().toEpochMilli()) {
            Thread.sleep(10L);
        }
        storage.removeExpiredComponents();
        Assertions.assertThat(storage.getAllComponents())
                .containsExactlyInAnyOrder(
                        examples.get(0), examples.get(1)
                );
    }

    @ParameterizedTest
    @ArgumentsSource(StorageArguments.class)
    void checkInsertion(final ComponentStorage storage) {
        final var examples = EXAMPLES.get();
        examples.forEach(storage::insertComponent);
        Assertions.assertThat(storage.getAllComponents())
                .containsExactlyInAnyOrderElementsOf(examples);
    }

    @ParameterizedTest
    @ArgumentsSource(StorageArguments.class)
    void checkPermanentDeletion(final ComponentStorage storage) {
        final var examples = EXAMPLES.get();
        examples.forEach(storage::insertComponent);
        storage.removePermanentComponents();
        Assertions.assertThat(storage.getAllComponents())
                .containsExactlyInAnyOrder(
                        examples.get(1), examples.get(2)
                );
    }

    private static final class StorageArguments implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws IOException {
            final File location = File.createTempFile("components", ".db");
            location.deleteOnExit();
            final SQLiteDataSource source = new SQLiteDataSource();
            source.setUrl("jdbc:sqlite:" + location.getAbsolutePath());

            Flyway.configure()
                    .locations("classpath:com/matyrobbrt/jdahelper/components/db")
                    .dataSource(source)
                    .loggers()
                    .load()
                    .migrate();

            return Stream.of(
                    Arguments.of(
                            ComponentStorage.inMemory(new HashMap<>())
                    ),
                    Arguments.of(
                            ComponentStorage.sql(Jdbi.create(source), "components")
                    )
            );
        }
    }
}
