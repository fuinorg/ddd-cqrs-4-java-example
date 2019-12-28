package org.fuin.cqrs4j.example.spring.query.handler;

import org.fuin.cqrs4j.EventHandler;
import org.fuin.cqrs4j.example.spring.query.controller.PersonRepository;
import org.fuin.cqrs4j.example.spring.query.domain.Person;
import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.ddd4j.ddd.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles the {@link PersonCreatedEvent}.
 */
@Component
public class PersonCreatedEventHandler implements EventHandler<PersonCreatedEvent> {
    
    private static final Logger LOG = LoggerFactory.getLogger(PersonCreatedEventHandler.class);

    @Autowired
    private PersonRepository repo;

    @Override
    public EventType getEventType() {
        return PersonCreatedEvent.TYPE;
    }

    @Override
    @Transactional
    public void handle(final PersonCreatedEvent event) {
        LOG.info("Handle " + event);
        final PersonId personId = event.getEntityId();
        if (repo.findById(personId.asString()) == null) {
            repo.save(new Person(personId, event.getName()));
        }
    }

}