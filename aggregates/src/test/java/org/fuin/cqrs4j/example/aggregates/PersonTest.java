package org.fuin.cqrs4j.example.aggregates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Optional;
import java.util.UUID;

import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.junit.Test;

/**
 * Test for the {@link Person} class. 
 */
public class PersonTest {

    @Test
    public final void testCreateOK() throws DuplicatePersonNameException {

        // PREPARE
        final PersonId personId = new PersonId(UUID.fromString("f645969a-402d-41a9-882b-d2d8000d0f43"));
        final PersonName personName = new PersonName("Peter Parker");

        // TEST
        final Person testee = new Person(personId, personName, pid -> { return Optional.empty(); }) ;

        // VERIFY
        assertThat(testee.getUncommittedChanges()).hasSize(1);
        assertThat(testee.getUncommittedChanges().get(0)).isInstanceOf(PersonCreatedEvent.class);
        final PersonCreatedEvent event = (PersonCreatedEvent) testee.getUncommittedChanges().get(0);
        assertThat(event.getEntityId()).isEqualTo(personId);
        assertThat(event.getName()).isEqualTo(personName);

    }
    
    
    @Test
    public final void testCreateDuplicateName() {

        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Peter Parker");
        final PersonId otherId = new PersonId(UUID.randomUUID());

        // TEST & VERIFY
        try {
            new Person(personId, personName, pid -> { return Optional.of(otherId); }) ;
            fail("Excpected duplicate name exception");
        } catch (final DuplicatePersonNameException ex) {
            assertThat(ex.getMessage()).isEqualTo("The name 'Peter Parker' already exists: " + otherId);
        }
        
    }
    
}
