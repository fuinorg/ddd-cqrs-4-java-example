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
package org.fuin.cqrs4j.example.spring.command.domain;

import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.fuin.objects4j.common.ExceptionShortIdentifable;

/**
 * A name that should be unique does already exist.
 */
public final class DuplicatePersonNameException extends Exception implements ExceptionShortIdentifable {

    private static final long serialVersionUID = 1000L;

    private String sid;
    
    private PersonId personId;

    private PersonName name;

    /**
     * Constructor with mandatory data.
     * 
     * @param personId
     *            Identifier of the resource that caused the problem.
     * @param name
     *            Name of the resource that caused the problem.
     */
    public DuplicatePersonNameException(@NotNull final PersonId personId, @NotNull final PersonName name) {
        super("The name '" + name + "' already exists: " + personId.asString());
        this.sid = "DUPLICATE_PERSON_NAME";
        this.personId = personId;
        this.name = name;
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

    /**
     * Returns the name that already exists.
     * 
     * @return Name.
     */
    public final PersonName getName() {
        return name;
    }

}