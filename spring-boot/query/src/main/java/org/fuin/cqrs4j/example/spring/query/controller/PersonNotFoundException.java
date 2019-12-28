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
package org.fuin.cqrs4j.example.spring.query.controller;

import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.objects4j.common.ExceptionShortIdentifable;

/**
 * A person with the given identifier is unknown.
 */
public final class PersonNotFoundException extends Exception implements ExceptionShortIdentifable {

    private static final long serialVersionUID = 1000L;

    private String sid;
    
    private PersonId personId;

    /**
     * Constructor with mandatory data.
     * 
     * @param personId
     *            Identifier of the resource that caused the problem.
     */
    public PersonNotFoundException(@NotNull final PersonId personId) {
        super("Person not found: " + personId.asString());
        this.sid = "PERSON_NOT_FOUND";
        this.personId = personId;
    }

    @Override
    public final String getShortId() {
        return sid;
    }
    
    /**
     * Returns the identifier of the entity that has the name.
     * 
     * @return Identifier.
     */
    public final PersonId getPersonId() {
        return personId;
    }

}