/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see
 * http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.quarkus.query.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.fuin.cqrs4j.EventHandler;
import org.fuin.cqrs4j.example.quarkus.query.domain.QryPerson;
import org.fuin.cqrs4j.example.quarkus.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.quarkus.shared.PersonId;
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
    EntityManager em;
    
    @Override
    public EventType getEventType() {
        return PersonCreatedEvent.TYPE;
    }

    @Override
    @Transactional
    public void handle(final PersonCreatedEvent event) {
        LOG.info("Handle " + event.getClass().getSimpleName() + ": " + event);
        final PersonId personId = event.getEntityId();
        if (em.find(QryPerson.class, personId.asString()) == null) {
            em.persist(new QryPerson(personId, event.getName()));
        }
    }

}