package org.fuin.cqrs4j.example.javasecdi.qry.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.fuin.cqrs4j.EventHandler;
import org.fuin.cqrs4j.example.javasecdi.qry.domain.QryPerson;
import org.fuin.cqrs4j.example.javasecdi.qry.domain.QryPersonRepository;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.ddd.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the {@link PersonCreatedEvent}.
 */
@ApplicationScoped
public class PersonCreatedEventHandler implements EventHandler<PersonCreatedEvent> {
    
    private static final Logger LOG = LoggerFactory.getLogger(PersonCreatedEventHandler.class);

    @Inject
    private QryPersonRepository repo;

    @Override
    public EventType getEventType() {
        return PersonCreatedEvent.TYPE;
    }

    @Override
    @Transactional
    public void handle(final PersonCreatedEvent event) {
        LOG.info("Handle " + event);
        final PersonId personId = event.getEntityId();
        if (repo.findBy(personId.asString()) == null) {
            repo.save(new QryPerson(personId, event.getName()));
        }

    }

}