package com.matyrobbrt.jdahelper.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An interface implemented by objects to provide functions (such as predicates, functions and consumers) that also
 * consume the {@code this} object.
 *
 * @param <SELF> the type of the object implementing this class
 */
@SuppressWarnings("unchecked")
public interface SelfFunctions<SELF> {
    /**
     * Creates a consumer that also consumes this object.
     *
     * @param biConsumer a consumer accepting the {@code T} type and the {@code SELF} type
     * @param <T>        the consumer type
     * @return the consumer
     */
    default <T> Consumer<T> asConsumer(BiConsumer<T, SELF> biConsumer) {
        return t -> biConsumer.accept(t, (SELF) this);
    }

    /**
     * Creates a predicate that also accepts this object.
     *
     * @param biPredicate a predicate accepting the {@code T} type and the {@code SELF} type
     * @param <T>         the predicate type
     * @return the predicate
     */
    default <T> Predicate<T> asPredicate(BiPredicate<T, SELF> biPredicate) {
        return t -> biPredicate.test(t, (SELF) this);
    }

    /**
     * Creates a function that also accepts this object.
     *
     * @param biFunction a function accepting the {@code T} type and the {@code SELF} type, returning the {@code R} type
     * @param <T>        the function input type
     * @param <R>        the function return type
     * @return the function
     */
    default <T, R> Function<T, R> asFunction(BiFunction<T, SELF, R> biFunction) {
        return t -> biFunction.apply(t, (SELF) this);
    }

}
