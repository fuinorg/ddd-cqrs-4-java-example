package org.fuin.cqrs4j.example.spring.query.views.personlist;

import javax.persistence.EntityManager;

import org.fuin.cqrs4j.EventHandler;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.ddd.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles the {@link PersonCreatedEvent}.
 */
@Component
public class PersonCreatedEventHandler implements EventHandler<PersonCreatedEvent> {
    
    private static final Logger LOG = LoggerFactory.getLogger(PersonCreatedEventHandler.class);

    @Autowired
    private EntityManager em;

    @Override
    public EventType getEventType() {
        return PersonCreatedEvent.TYPE;
    }

    @Override    
    public void handle(final PersonCreatedEvent event) {
        LOG.info("Handle " + event);
        final PersonId personId = event.getEntityId();
        if (em.find(PersonListEntry.class, personId.asString()) == null) {
            em.persist(new PersonListEntry(personId, event.getName()));
        }
    }

}