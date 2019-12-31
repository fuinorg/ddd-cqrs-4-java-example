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

import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.objects4j.common.ExceptionShortIdentifable;


/**
 * A name that should be unique does already exist.
 */
public final class DuplicatePersonNameException extends Exception implements ExceptionShortIdentifable {

    private static final long serialVersionUID = 1000L;

    private static final String SHORT_ID = "DUPLICATE_PERSON_NAME"; 
    
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
        this.personId = personId;
        this.name = name;
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

	@Override
	public final String getShortId() {
		return SHORT_ID;
	}

}