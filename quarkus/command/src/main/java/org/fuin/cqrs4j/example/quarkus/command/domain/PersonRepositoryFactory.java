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
package org.fuin.cqrs4j.example.quarkus.command.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import org.fuin.cqrs4j.example.aggregates.PersonRepository;
import org.fuin.esc.esjc.IESJCEventStore;

/**
 * CDI factory that creates an event store connection and repositories.
 */
@ApplicationScoped
public class PersonRepositoryFactory {

    /**
     * Creates a repository.
     * 
     * @param eventStore
     *            Event store implementation.
     * 
     * @return Request scoped project repository.
     */
    @Produces
    @Dependent
    public PersonRepository create(final IESJCEventStore eventStore) {
        return new PersonRepository(eventStore);
    }

}
