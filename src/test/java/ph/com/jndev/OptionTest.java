package ph.com.jndev;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
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

        expectSome(intValue, 1);
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
