package com.matyrobbrt.jdahelper.components;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

public interface ComponentLifespan {
    ComponentLifespan TEMPORARY = expiringAt(null);
    ComponentLifespan PERMANENT = new ComponentLifespan() {
        @Override
        public boolean isTemporary() {
            return false;
        }

        @Nullable
        @Override
        public Instant getExpiryTime() {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            return obj instanceof ComponentLifespan lifespan &&
                    ComponentLifespan.instantsSame(lifespan.getExpiryTime(), getExpiryTime()) &&
                    isTemporary() == lifespan.isTemporary();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getExpiryTime(), isTemporary());
        }

        @Override
        public String toString() {
            return "PermanentComponentLifespan";
        }
    };

    boolean isTemporary();

    @Nullable
    Instant getExpiryTime();

    static ComponentLifespan permanent() {
        return PERMANENT;
    }

    static ComponentLifespan expiringIn(TemporalAmount expiring) {
        return expiringAt(Instant.now().plus(expiring));
    }

    static ComponentLifespan temporary() {
        return TEMPORARY;
    }

    static ComponentLifespan expiringAt(@Nullable Instant expiryTime) {
        return new ComponentLifespan() {
            @Override
            public boolean isTemporary() {
                return true;
            }

            @Nullable
            @Override
            public Instant getExpiryTime() {
                return expiryTime;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) return true;
                return obj instanceof ComponentLifespan lifespan &&
                        ComponentLifespan.instantsSame(getExpiryTime(), lifespan.getExpiryTime()) &&
                        isTemporary() == lifespan.isTemporary();
            }

            @Override
            public int hashCode() {
                return Objects.hash(getExpiryTime(), isTemporary());
            }

            @Override
            public String toString() {
                return "TemporaryComponentLifespan[expiringAt " + getExpiryTime() + "]";
            }
        };
    }

    private static boolean instantsSame(@Nullable Instant o, @Nullable Instant o2) {
        return o == o2 || (o != null && o2 != null && o.toEpochMilli() == o2.toEpochMilli());
    }
}
