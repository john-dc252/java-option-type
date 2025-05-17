package ph.com.jndev;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Rust option enum in Java
 *
 * @param <T> type of the wrapped value
 */
public sealed interface Option<T> {
    /**
     * @param value the value to be wrapped in an {@link Option<T>}
     * @param <T>   type of value wrapped in the returned {@link Option<T>}
     * @return a new instance of {@link Some<T>} containing the provided {@code value}
     */
    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    /**
     * @param <T> type of value wrapped in the returned {@link Option<T>}
     * @return the singleton instance of {@link None<T>}
     */
    static <T> Option<T> none() {
        return None.singleton();
    }

    /**
     * @param optional
     * @param <T>      type of value wrapped in the returned {@link Option<T>}
     * @return a new instance of {@link Option<T>} derived from {@code optional}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> Option<T> fromOptional(Optional<T> optional) {
        return optional.map(Option::some).orElse(none());
    }

    /**
     * @return whether {@code this} is a {@link Some<T>}
     */
    default boolean isSome() {
        return this instanceof Some<T>;
    }

    /**
     * @return whether {@code this} is a {@link None<T>}
     */
    default boolean isNone() {
        return this instanceof None<T>;
    }

    /**
     * @param other value to be returned if {@code this} is a {@link None<T>}
     * @return the wrapped value or {@code other} if {@code this} is a {@link None<T>}
     */
    default T unwrapOr(T other) {
        return this instanceof Some(T v) ? v : other;
    }

    /**
     * @param otherFn value supplier that should return a value to return if {@code this} is a {@link None<T>}
     * @return the wrapped value or the returned value of {@code otherFn} if {@code this} is a {@link None<T>}
     */
    default T unwrapOr(Supplier<T> otherFn) {
        return this instanceof Some(T v) ? v : Objects.requireNonNull(otherFn).get();
    }

    /**
     * @param mapFn the mapping function
     * @param <R>   type of the value returned by {@code mapFn}
     * @return a {@link Some<R>} wrapping the value returned by applying {@code mapFn} on the value wrapped by {@code this},
     * or a {@link None<R>} if {@code this} is a {@link None<T>} or the value returned by {@code mapFn} is {@code null}
     */
    default <R> Option<R> map(Function<T, R> mapFn) {
        return this instanceof Some<T>(T v)
                ? Objects.requireNonNull(mapFn).<Option<R>>andThen(o -> o == null ? none() : some(o)).apply(v)
                : none();
    }

    /**
     * @param flatMapFn
     * @param <R>
     * @return
     */
    default <R> Option<R> flatMap(Function<T, Option<R>> flatMapFn) {
        return this instanceof Some<T>(T v)
                ? Objects.requireNonNull(flatMapFn).andThen(Objects::requireNonNull).apply(v)
                : none();
    }

    /**
     * @param filterFn
     * @return
     */
    default Option<T> filter(Predicate<T> filterFn) {
        return this instanceof Some(T v) && filterFn.test(v) ? this : none();
    }

    /**
     * @return the wrapped value if {@code this} is a {@link Some<T>}
     * @throws NullPointerException if {@code this} is a {@link None<T>}
     */
    default T unwrap() {
        if (this instanceof Some<T>(T value)) return value;
        throw new NullPointerException("Tried to unwrap a None object");
    }

    /**
     * @return a {@link Stream<T>} with the value wrapped by {@code this} as its only element or an empty {@link Stream<T>} if {@code this} is a {@link None<T>}
     */
    default Stream<T> stream() {
        return this instanceof Some<T>(T v) ? Stream.of(v) : Stream.of();
    }

    /**
     * @param value
     * @param <T>
     */
    record Some<T>(T value) implements Option<T> {
        public Some {
            Objects.requireNonNull(value);
        }

        @Override
        public String toString() {
            return "Some(" + value.toString() + ")";
        }
    }

    /**
     * @param <T>
     */
    final class None<T> implements Option<T> {
        private static final None<?> NONE = new None<>();

        private None() {}

        @SuppressWarnings("unchecked")
        private static <T> Option<T> singleton() {
            return (Option<T>) NONE;
        }

        @Override
        public String toString() {
            return "None";
        }
    }
}
