package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.EventHandler;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.core.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the {@link PersonCreatedEvent}.
 */
@ApplicationScoped
public class PersonCreatedEventHandler implements EventHandler<PersonCreatedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonCreatedEventHandler.class);

    @Inject
    EntityManager em;

    @Override
    public EventType getEventType() {
        return PersonCreatedEvent.TYPE;
    }

    @Override
    public void handle(final PersonCreatedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final PersonId personId = event.getEntityId();
        if (em.find(PersonListEntry.class, personId.asString()) == null) {
            em.persist(new PersonListEntry(personId, event.getName()));
        }
    }

}