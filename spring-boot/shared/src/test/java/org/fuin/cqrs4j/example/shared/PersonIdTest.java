package org.fuin.cqrs4j.example.shared;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.ddd4j.core.EntityType;
import org.fuin.ddd4j.core.StringBasedEntityType;
import org.fuin.objects4j.common.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for {@link org.fuin.cqrs4j.example.spring.shared.PersonId}.
 */
public final class PersonIdTest {

    private static final String PERSON_UUID = "84565d62-115e-4502-b7c9-38ad69c64b05";

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(org.fuin.cqrs4j.example.spring.shared.PersonId.class).suppress(Warning.NONFINAL_FIELDS).withNonnullFields("entityType", "uuid")
                .withPrefabValues(EntityType.class, new StringBasedEntityType("A"), new StringBasedEntityType("B")).verify();
    }

    @Test
    public void testValueOf() {
        final org.fuin.cqrs4j.example.spring.shared.PersonId personId = org.fuin.cqrs4j.example.spring.shared.PersonId.valueOf(PERSON_UUID);

        assertThat(personId.asString()).isEqualTo(PERSON_UUID);

    }

    @Test
    public void testValueOfIllegalArgumentCharacter() {
        try {
            org.fuin.cqrs4j.example.spring.shared.PersonId.valueOf("abc");
            fail();
        } catch (final ConstraintViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo("The argument 'value' is not valid: 'abc'");
        }
    }

    @Test
    public final void testConverterUnmarshal() throws Exception {

        // PREPARE
        final String personIdValue = PERSON_UUID;

        // TEST
        final org.fuin.cqrs4j.example.spring.shared.PersonId personId = new org.fuin.cqrs4j.example.spring.shared.PersonId.Converter().adaptFromJson(UUID.fromString(PERSON_UUID));

        // VERIFY
        assertThat(personId.asString()).isEqualTo(personIdValue);
    }

    @Test
    public void testConverterMarshal() throws Exception {

        final org.fuin.cqrs4j.example.spring.shared.PersonId personId = org.fuin.cqrs4j.example.spring.shared.PersonId.valueOf(PERSON_UUID);

        // TEST
        final UUID uuid = new PersonId.Converter().adaptToJson(personId);

        // VERIFY
        assertThat(uuid).isEqualTo(UUID.fromString(PERSON_UUID));
    }

}
