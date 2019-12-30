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
package org.fuin.cqrs4j.example.aggregates;

import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.ddd4j.esrepo.EventStoreRepository;
import org.fuin.esc.api.EventStore;

/**
 * Event sourced repository for storing a {@link Person} aggregate.
 */
@NotThreadSafe
public class PersonRepository extends EventStoreRepository<PersonId, Person> {

    /**
     * Constructor all mandatory data.
     * 
     * @param eventStore
     *            Event store.
     */
    public PersonRepository(final EventStore eventStore) {
        super(eventStore);
    }

    @Override
    @NotNull
    public Class<Person> getAggregateClass() {
        return Person.class;
    }

    @Override
    @NotNull
    public EntityType getAggregateType() {
        return PersonId.TYPE;
    }

    @Override
    @NotNull
    public Person create() {
        return new Person();
    }

    @Override
    @NotNull
    public String getIdParamName() {
        return "personId";
    }

}