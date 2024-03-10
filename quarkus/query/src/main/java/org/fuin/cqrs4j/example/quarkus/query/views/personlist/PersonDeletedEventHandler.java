package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.EventHandler;
import org.fuin.cqrs4j.example.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.core.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the {@link PersonDeletedEvent}.
 */
@ApplicationScoped
public class PersonDeletedEventHandler implements EventHandler<PersonDeletedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonDeletedEventHandler.class);

    @Inject
    EntityManager em;

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