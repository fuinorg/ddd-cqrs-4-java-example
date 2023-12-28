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

import java.io.Serializable;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.ddd4j.ddd.AbstractAggregateRoot;
import org.fuin.ddd4j.ddd.ApplyEvent;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.objects4j.common.Contract;

/**
 * Represents a natural person.
 */
public class Person extends AbstractAggregateRoot<PersonId> implements Serializable {

    private static final long serialVersionUID = 1000L;

    @NotNull
    private PersonId id;

    /**
     * Default constructor that is mandatory for aggregate roots.
     */
    public Person() {
        super();
    }

    /**
     * Constructor with all data.
     * 
     * @param id
     *            Unique identifier of the person.
     * @param name
     *            Unique name of the person.
     * @param service
     *            Service required by the method.
     * 
     * @throws DuplicatePersonNameException
     *             The name already exists for another person.
     */
    public Person(@NotNull final PersonId id, @NotNull final PersonName name, final CreatePersonService service)
            throws DuplicatePersonNameException {
        super();

        // VERIFY PRECONDITIONS
        Contract.requireArgNotNull("id", id);
        Contract.requireArgNotNull("name", name);

        // VERIFY BUSINESS RULES

        // Rule 1: The name of the person must be unique
        final Optional<PersonId> otherId = service.loadPersonIdByName(name);
        if (otherId.isPresent()) {
            throw new DuplicatePersonNameException(otherId.get(), name);
        }

        // CREATE EVENT
        apply(new PersonCreatedEvent(id, name));

    }

    @Override
    public PersonId getId() {
        return id;
    }

    @Override
    public EntityType getType() {
        return PersonId.TYPE;
    }

    @ApplyEvent
    public void applyEvent(final PersonCreatedEvent event) {
        this.id = event.getEntityId();
    }

    /**
     * Service for the constructor.
     */
    public static interface CreatePersonService {

        /**
         * Loads the person's identifier for a given name.
         * 
         * @param name
         *            Person's name.
         * 
         * @return Office identifier or empty if not found.
         */
        public Optional<PersonId> loadPersonIdByName(@NotNull PersonName name);

    }

}
