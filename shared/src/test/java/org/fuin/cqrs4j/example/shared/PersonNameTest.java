/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.shared;


import static org.assertj.core.api.Assertions.assertThat;

import org.fuin.objects4j.common.ConstraintViolationException;
import org.fuin.utils4j.Utils4J;
import org.junit.Assert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

// CHECKSTYLE:OFF
public final class PersonNameTest {

    @Test
    public void testSerialize() {
        final PersonName original = new PersonName("Peter Parker");
        final PersonName copy = Utils4J.deserialize(Utils4J.serialize(original));
        assertThat(original).isEqualTo(copy);
    }

    @Test
    public void testHashCodeEquals() {
        EqualsVerifier.forClass(PersonName.class).suppress(Warning.NULL_FIELDS).withRedefinedSuperclass().verify();
    }

    @Test
    public void testMarshalJson() throws Exception {

        // PREPARE
        final String str = "Peter Parker";
        final PersonName testee = new PersonName(str);

        // TEST & VERIFY
        assertThat(new PersonName.Converter().adaptToJson(testee)).isEqualTo(str);
        assertThat(new PersonName.Converter().adaptToJson(null)).isNull();

    }

    @Test
    public void testUnmarshalJson() throws Exception {

        // PREPARE
        final String str = "Peter Parker";
        final PersonName testee = new PersonName(str);

        // TEST & VERIFY
        assertThat(new PersonName.Converter().adaptFromJson(str)).isEqualTo(testee);
        assertThat(new PersonName.Converter().adaptFromJson(null)).isNull();

    }

    @Test
    public void testIsValid() {

        assertThat(PersonName.isValid(null)).isTrue();
        assertThat(PersonName.isValid("Peter Parker")).isTrue();

        assertThat(PersonName.isValid("")).isFalse();
        assertThat(PersonName.isValid("123456789.123456789.123456789.123456789.123456789."
                + "123456789.123456789.123456789.123456789.123456789." + "12345")).isFalse();

    }

    @Test
    public void testRequireArgValid() {

        PersonName.requireArgValid("a", "Peter Parker");
        PersonName.requireArgValid("b", null);

        try {
            PersonName.requireArgValid("c", "");
            Assert.fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo("The argument 'c' is not valid: ''");
        }

        try {
            PersonName.requireArgValid("d", "123456789.123456789.123456789.123456789.123456789."
                    + "123456789.123456789.123456789.123456789.123456789." + "12345");
            Assert.fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage())
                    .isEqualTo("The argument 'd' is not valid: '" + "123456789.123456789.123456789.123456789.123456789."
                            + "123456789.123456789.123456789.123456789.123456789." + "12345" + "'");
        }

    }

    @Test
    public void testValidator() {

        assertThat(new PersonName.Validator().isValid(null, null)).isTrue();
        assertThat(new PersonName.Validator().isValid("Peter Parker", null)).isTrue();

        assertThat(new PersonName.Validator().isValid("", null)).isFalse();
        assertThat(new PersonName.Validator().isValid("123456789.123456789.123456789.123456789.123456789."
                + "123456789.123456789.123456789.123456789.123456789." + "12345", null)).isFalse();

    }

    @Test
    public void testValueObjectConverter() {

        assertThat(new PersonName.Converter().getBaseTypeClass()).isEqualTo(String.class);
        assertThat(new PersonName.Converter().getValueObjectClass()).isEqualTo(PersonName.class);
        assertThat(new PersonName.Converter().isValid(null)).isTrue();
        assertThat(new PersonName.Converter().isValid("Peter Parker")).isTrue();

        assertThat(new PersonName.Converter().isValid("123456789.123456789.123456789.123456789.123456789."
                + "123456789.123456789.123456789.123456789.123456789." + "12345")).isFalse();

    }

}
