package ph.com.jndev;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;
import static ph.com.jndev.Option.none;
import static ph.com.jndev.Option.some;

public class OptionTest {
    @Test
    public void testNoneValue() {
        final var noneValue = none();

        assertThat(noneValue.isSome(), is(false));
        assertThat(noneValue.isNone(), is(true));

        assertThrows(NullPointerException.class, noneValue::unwrap);
    }

    @Test
    public void testOptionType() {
        final var intValue = some(1);

        assertThat(intValue.isSome(), is(true));
        assertThat(intValue.isNone(), is(false));
        assertThat(intValue.unwrap(), is(1));
    }

    @Test
    public void testOptionMap() {
        final var intValue = some(1);
        final var stringValue = intValue.map(String::valueOf);

        assertThat(stringValue.unwrap(), is("1"));
    }
}
