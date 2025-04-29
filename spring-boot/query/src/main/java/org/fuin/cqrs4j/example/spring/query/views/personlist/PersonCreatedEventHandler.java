package org.fuin.cqrs4j.example.spring.query.views.personlist;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.JpaEventHandler;
import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.ddd4j.core.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the {@link PersonCreatedEvent}.
 */
public class PersonCreatedEventHandler implements JpaEventHandler<PersonCreatedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonCreatedEventHandler.class);

    @Override
    public EventType getEventType() {
        return PersonCreatedEvent.TYPE;
    }

    @Override
    public void handle(final EntityManager em, final PersonCreatedEvent event) {
        LOG.info("Handle {}", event);
        final PersonId personId = event.getEntityId();
        if (em.find(PersonListEntry.class, personId.asString()) == null) {
            em.persist(new PersonListEntry(personId, event.getName()));
        }
    }

}