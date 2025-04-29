package org.fuin.cqrs4j.example.spring.command.domain;

import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.fuin.ddd4j.core.AggregateDeletedException;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for the {@link Person} class.
 */
class PersonTest {

    @Test
    void testCreateOK() throws DuplicatePersonNameException {

        // PREPARE
        final PersonId personId = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
        final PersonName personName = new PersonName("Peter Parker");

        // TEST
        final Person testee = new Person(personId, personName, pid -> {
            return Optional.empty();
        });

        // VERIFY
        assertThat(testee.getUncommittedChanges()).hasSize(1);
        assertThat(testee.getUncommittedChanges().get(0)).isInstanceOf(PersonCreatedEvent.class);
        final PersonCreatedEvent event = (PersonCreatedEvent) testee.getUncommittedChanges().get(0);
        assertThat(event.getEntityId()).isEqualTo(personId);
        assertThat(event.getAggregateVersionInteger()).isEqualTo(0);
        assertThat(event.getName()).isEqualTo(personName);

    }

    @Test
    void testCreateDuplicateName() {

        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Peter Parker");
        final PersonId otherId = new PersonId(UUID.randomUUID());

        // TEST & VERIFY
        try {
            new Person(personId, personName, pid -> {
                return Optional.of(otherId);
            });
            fail("Excpected duplicate name exception");
        } catch (final DuplicatePersonNameException ex) {
            assertThat(ex.getMessage()).isEqualTo("The name 'Peter Parker' already exists: " + otherId);
        }

    }

    @Test
    void testDeleteOK() throws DuplicatePersonNameException, AggregateDeletedException {

        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Peter Parker");
        final PersonId otherId = new PersonId(UUID.randomUUID());
        final Person testee = new Person(personId, personName, pid -> {
            return Optional.empty();
        });
        testee.markChangesAsCommitted();

        // TEST
        testee.delete();

        //VERIFY
        assertThat(testee.getUncommittedChanges()).hasSize(1);
        assertThat(testee.getUncommittedChanges().get(0)).isInstanceOf(PersonDeletedEvent.class);
        final PersonDeletedEvent event = (PersonDeletedEvent) testee.getUncommittedChanges().get(0);
        assertThat(event.getEntityId()).isEqualTo(personId);
        assertThat(event.getAggregateVersionInteger()).isEqualTo(1);
        assertThat(event.getName()).isEqualTo(personName);

    }

}
