package com.matyrobbrt.jdahelper.components.storage;

import com.matyrobbrt.jdahelper.components.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
@SuppressWarnings("ClassCanBeRecord")
public class InMemoryComponentStorage implements ComponentStorage {

    private final Map<UUID, ComponentInstance> map;

    InMemoryComponentStorage(Map<UUID, ComponentInstance> map) {
        this.map = map;
    }

    @Override
    public void insertComponent(Component component) {
        map.put(component.uuid(), new ComponentInstance(component));
    }

    @Override
    public void removeComponent(UUID id) {
        map.remove(id);
    }

    @Override
    public void removeComponentsLastUsedBefore(Instant before) {
        map.values().removeIf(in -> in.component.lifespan() != Component.Lifespan.PERMANENT && in.lastUsed.isBefore(before));
    }

    @Override
    public @NotNull Optional<Component> getComponent(UUID id) {
        return Optional.ofNullable(map.get(id)).map(in -> in.component);
    }

    @Override
    public void updateArguments(UUID id, List<String> newArguments) {
        final var lastComp = map.get(id);
        if (lastComp != null) {
            lastComp.component = new Component(lastComp.component.featureId(), lastComp.component.uuid(), newArguments, lastComp.component.lifespan());
            map.put(id, lastComp);
        }
    }

    @Override
    public void setLastUsed(UUID id, Instant lastUsed) {
        final var c = this.map.get(id);
        if (c != null)
            c.lastUsed = lastUsed;
    }

    public static class ComponentInstance {
        public Component component;
        public Instant lastUsed;

        public ComponentInstance(Component component) {
            this.component = component;
            this.lastUsed = Instant.now();
        }
    }
}
