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
     * @param value the value to be wrapped in an {@link Option}
     * @param <T>   type of value wrapped in the returned {@link Option}
     * @return a new instance of {@link Some} containing the provided {@code value}
     */
    static <T> Option<T> some(T value) {
        return new Some<>(value);
    }

    /**
     * @param <T> type of value wrapped in the returned {@link Option}
     * @return the singleton instance of {@link None}
     */
    static <T> Option<T> none() {
        return None.singleton();
    }

    /**
     * @param optional the source {@link Optional}
     * @param <T>      type of value wrapped in the returned {@link Option}
     * @return a new instance of {@link Option} derived from {@code optional}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> Option<T> fromOptional(Optional<T> optional) {
        return optional.map(Option::some).orElse(none());
    }

    /**
     * @return whether {@code this} is a {@link Some}
     */
    default boolean isSome() {
        return this instanceof Some<T>;
    }

    /**
     * @return whether {@code this} is a {@link None}
     */
    default boolean isNone() {
        return this instanceof None<T>;
    }

    /**
     * @param other value to be returned if {@code this} is a {@link None}
     * @return the wrapped value or {@code other} if {@code this} is a {@link None}
     */
    default T unwrapOr(T other) {
        return this instanceof Some(T v) ? v : other;
    }

    /**
     * @param otherFn value supplier that should return a value to return if {@code this} is a {@link None}
     * @return the wrapped value or the returned value of {@code otherFn} if {@code this} is a {@link None}
     */
    default T unwrapOr(Supplier<T> otherFn) {
        return this instanceof Some(T v) ? v : Objects.requireNonNull(otherFn).get();
    }

    /**
     * @param mapFn the mapping function
     * @param <R>   type of the value returned by {@code mapFn}
     * @return a {@link Some} wrapping the value returned by applying {@code mapFn} on the value wrapped by {@code this},
     * or a {@link None} if {@code this} is a {@link None} or the value returned by {@code mapFn} is {@code null}
     */
    default <R> Option<R> map(Function<T, R> mapFn) {
        return this instanceof Some<T>(T v)
                ? Objects.requireNonNull(mapFn).<Option<R>>andThen(o -> o == null ? none() : some(o)).apply(v)
                : none();
    }

    /**
     * @param flatMapFn the mapping function
     * @param <R>       type of the value wrapped by the returned {@link Option}
     * @return the {@link Option} returned by the {@code flatMapFn} if this {@link Option} was a {@link Some}
     */
    default <R> Option<R> flatMap(Function<T, Option<R>> flatMapFn) {
        return this instanceof Some<T>(T v)
                ? Objects.requireNonNull(flatMapFn).andThen(Objects::requireNonNull).apply(v)
                : none();
    }

    /**
     * @param filterFn the filter predicate
     * @return this same {@link Option} if it is a {@link Some} and the wrapped value matches the provided predicate and
     * a {@link None} if it doesn't.
     */
    default Option<T> filter(Predicate<T> filterFn) {
        return this instanceof Some(T v) && filterFn.test(v) ? this : none();
    }

    /**
     * @return the wrapped value if {@code this} is a {@link Some}
     * @throws NullPointerException if {@code this} is a {@link None}
     */
    default T unwrap() {
        if (this instanceof Some<T>(T value)) return value;
        throw new NullPointerException("Tried to unwrap a None object");
    }

    /**
     * @return a {@link Stream} with the value wrapped by {@code this} as its only element or an empty {@link Stream} if {@code this} is a {@link None}
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
