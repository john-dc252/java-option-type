package ph.com.jndev;

import org.testng.annotations.Test;

import java.util.Optional;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.testng.Assert.expectThrows;
import static ph.com.jndev.Option.none;
import static ph.com.jndev.Option.some;

public class OptionTest {
    @Test
    public void testNoneValue() {
        final var noneValue = none();

        expectNone(noneValue);
    }

    @Test
    public void testSomeValue() {
        final var intValue = some(1);

        expectSome(intValue, 2);
    }

    @Test
    public void testSomeUnwrapOr() {
        final var primitiveValue = 1;
        final var intValue = some(primitiveValue);
        final var result = intValue.unwrapOr(2);

        assertThat(result, is(primitiveValue));
    }

    @Test
    public void testNoneUnwrapOr() {
        final var intValue = none();
        final var defaultValue = 2;
        final var result = intValue.unwrapOr(defaultValue);

        assertThat(result, is(defaultValue));
    }

    @Test
    public void testSomeUnwrapOrFn() {
        final var primitiveValue = 1;
        final var intValue = some(primitiveValue);
        final var result = intValue.unwrapOr(() -> 2);

        assertThat(result, is(primitiveValue));
    }

    @Test
    public void testNoneUnwrapOrFn() {
        final var intValue = none();
        final var defaultValue = 2;
        final var result = intValue.unwrapOr(() -> defaultValue);

        assertThat(result, is(defaultValue));
    }

    @Test(description = "calling filter on a `Some` with a value that matches the predicate should return the same `Some`")
    public void testSomeFilterSome() {
        final var someValue = some(1);
        final var result = someValue.filter(Predicate.isEqual(1));

        assertThat(result, sameInstance(someValue));
    }

    @Test(description = "calling filter on a `Some` with a value that DOES NOT match the predicate should return `None`")
    public void testSomeFilterNone() {
        final var someValue = some(1);
        final var result = someValue.filter(Predicate.isEqual(2));

        assertThat(result, is(none()));
    }

    @Test(description = "calling filter on a `None` always returns `None`")
    public void testNoneFilterNone() {
        final var noneValue = none();
        final var result = noneValue.filter(Predicate.isEqual(1));

        assertThat(result, is(none()));
    }

    @Test
    public void testSomeMap() {
        final var intValue = some(1);
        final var stringValue = intValue.map(String::valueOf);

        expectSome(stringValue, "1");
    }

    @Test
    public void testNoneMap() {
        final var noneValue = none();
        final var stringValue = noneValue.map(String::valueOf);

        expectNone(stringValue);
    }

    @Test
    public void testSomeFlatMap() {
        final var intValue = some(1);
        final var stringValue = intValue.flatMap(v -> some(v.toString()));

        expectSome(stringValue, "1");
    }

    @Test
    public void testNoneFlatMap() {
        final var noneValue = none();
        final var stringValue = noneValue.flatMap(v -> some(v.toString()));

        expectNone(stringValue);
    }

    @Test
    public void testSomeFromOptional() {
        final var primitiveValue = 1;
        final var optional = Optional.of(primitiveValue);
        final var someValue = Option.fromOptional(optional);

        expectSome(someValue, primitiveValue);
    }

    @Test
    public void testNoneFromOptional() {
        final var optional = Optional.empty();
        final var someValue = Option.fromOptional(optional);

        expectNone(someValue);
    }

    @Test
    public void testToString() {
        final var someValue = some(1);
        final var noneValue = none();

        assertThat(someValue.toString(), is("Some(1)"));
        assertThat(noneValue.toString(), is("None"));
    }

    @Test
    public void testSomeStream() {
        final var someStream = some(1).stream();
        assertThat(someStream.toList(), contains(1));
    }

    @Test
    public void testNoneStream() {
        final var noneStream = none().stream();
        assertThat(noneStream.toList(), empty());
    }

    @Test(description = "`Some` should map to `None` if `mapFn` returns `null`")
    public void testSomeMapNull() {
        final var mappedValue = some(1).map(n -> null);
        expectNone(mappedValue);
    }

    private static <T> void expectSome(Option<T> optionValue, T unwrappedValue) {
        assertThat(optionValue.isSome(), is(true));
        assertThat(optionValue.isNone(), is(false));
        assertThat(optionValue.unwrap(), is(unwrappedValue));
    }

    private static <T> void expectNone(Option<T> noneValue) {
        assertThat(noneValue.isSome(), is(false));
        assertThat(noneValue.isNone(), is(true));
        expectThrows(NullPointerException.class, noneValue::unwrap);
    }
}
