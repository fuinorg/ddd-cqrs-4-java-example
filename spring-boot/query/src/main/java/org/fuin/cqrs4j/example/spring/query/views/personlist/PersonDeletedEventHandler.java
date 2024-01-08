package org.fuin.cqrs4j.example.spring.query.views.personlist;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.EventHandler;
import org.fuin.cqrs4j.example.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.ddd.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles the {@link PersonDeletedEvent}.
 */
@Component
public class PersonDeletedEventHandler implements EventHandler<PersonDeletedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonDeletedEventHandler.class);

    @Autowired
    private EntityManager em;

    @Override
    public EventType getEventType() {
        return PersonDeletedEvent.TYPE;
    }

    @Override
    public void handle(final PersonDeletedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final PersonId personId = event.getEntityId();
        final PersonListEntry entity = em.find(PersonListEntry.class, personId.asString());
        if (entity != null) {
            em.remove(entity);
        }
    }

}