package org.fuin.cqrs4j.example.spring.query.views.personlist;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.JpaEventHandler;
import org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.ddd4j.core.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the {@link PersonDeletedEvent}.
 */
public class PersonDeletedEventHandler implements JpaEventHandler<PersonDeletedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonDeletedEventHandler.class);

    @Override
    public EventType getEventType() {
        return PersonDeletedEvent.TYPE;
    }

    @Override
    public void handle(final EntityManager em, final PersonDeletedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final PersonId personId = event.getEntityId();
        final PersonListEntry entity = em.find(PersonListEntry.class, personId.asString());
        if (entity != null) {
            em.remove(entity);
        }
    }

}