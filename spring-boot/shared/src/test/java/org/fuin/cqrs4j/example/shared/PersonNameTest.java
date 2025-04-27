package org.fuin.cqrs4j.example.shared;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.fuin.objects4j.common.ConstraintViolationException;
import org.fuin.utils4j.Utils4J;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

// CHECKSTYLE:OFF
public final class PersonNameTest {

    @Test
    public void testSerialize() {
        final org.fuin.cqrs4j.example.spring.shared.PersonName original = new org.fuin.cqrs4j.example.spring.shared.PersonName("Peter Parker");
        final org.fuin.cqrs4j.example.spring.shared.PersonName copy = Utils4J.deserialize(Utils4J.serialize(original));
        assertThat(original).isEqualTo(copy);
    }

    @Test
    public void testHashCodeEquals() {
        EqualsVerifier.forClass(org.fuin.cqrs4j.example.spring.shared.PersonName.class).suppress(Warning.NULL_FIELDS).withRedefinedSuperclass().verify();
    }

    @Test
    public void testMarshalJson() throws Exception {

        // PREPARE
        final String str = "Peter Parker";
        final org.fuin.cqrs4j.example.spring.shared.PersonName testee = new org.fuin.cqrs4j.example.spring.shared.PersonName(str);

        // TEST & VERIFY
        assertThat(new org.fuin.cqrs4j.example.spring.shared.PersonName.Adapter().adaptToJson(testee)).isEqualTo(str);
        assertThat(new org.fuin.cqrs4j.example.spring.shared.PersonName.Adapter().adaptToJson(null)).isNull();

    }

    @Test
    public void testUnmarshalJson() throws Exception {

        // PREPARE
        final String str = "Peter Parker";
        final org.fuin.cqrs4j.example.spring.shared.PersonName testee = new org.fuin.cqrs4j.example.spring.shared.PersonName(str);

        // TEST & VERIFY
        assertThat(new org.fuin.cqrs4j.example.spring.shared.PersonName.Adapter().adaptFromJson(str)).isEqualTo(testee);
        assertThat(new org.fuin.cqrs4j.example.spring.shared.PersonName.Adapter().adaptFromJson(null)).isNull();

    }

    @Test
    public void testIsValid() {

        assertThat(org.fuin.cqrs4j.example.spring.shared.PersonName.isValid(null)).isTrue();
        assertThat(org.fuin.cqrs4j.example.spring.shared.PersonName.isValid("Peter Parker")).isTrue();

        assertThat(org.fuin.cqrs4j.example.spring.shared.PersonName.isValid("")).isFalse();
        assertThat(org.fuin.cqrs4j.example.spring.shared.PersonName.isValid(
                "123456789.123456789.123456789.123456789.123456789." + "123456789.123456789.123456789.123456789.123456789." + "12345"))
                .isFalse();

    }

    @Test
    public void testRequireArgValid() {

        org.fuin.cqrs4j.example.spring.shared.PersonName.requireArgValid("a", "Peter Parker");
        org.fuin.cqrs4j.example.spring.shared.PersonName.requireArgValid("b", null);

        try {
            org.fuin.cqrs4j.example.spring.shared.PersonName.requireArgValid("c", "");
            fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo("The argument 'c' is not valid: ''");
        }

        try {
            org.fuin.cqrs4j.example.spring.shared.PersonName.requireArgValid("d",
                    "123456789.123456789.123456789.123456789.123456789." + "123456789.123456789.123456789.123456789.123456789." + "12345");
            fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo("The argument 'd' is not valid: '" + "123456789.123456789.123456789.123456789.123456789."
                    + "123456789.123456789.123456789.123456789.123456789." + "12345" + "'");
        }

    }

    @Test
    public void testValidator() {

        assertThat(new org.fuin.cqrs4j.example.spring.shared.PersonName.Validator().isValid(null, null)).isTrue();
        assertThat(new org.fuin.cqrs4j.example.spring.shared.PersonName.Validator().isValid("Peter Parker", null)).isTrue();

        assertThat(new org.fuin.cqrs4j.example.spring.shared.PersonName.Validator().isValid("", null)).isFalse();
        assertThat(new PersonName.Validator().isValid(
                "123456789.123456789.123456789.123456789.123456789." + "123456789.123456789.123456789.123456789.123456789." + "12345",
                null)).isFalse();

    }

}
