/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.shared;

import javax.annotation.concurrent.Immutable;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotNull;

import org.fuin.ddd4j.ddd.AbstractDomainEvent;
import org.fuin.ddd4j.ddd.EntityIdPath;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.spi.SerializedDataType;
import org.fuin.objects4j.common.Contract;

/**
 * A new person was created in the system.
 */
@Immutable
public final class PersonCreatedEvent extends AbstractDomainEvent<PersonId> {

    private static final long serialVersionUID = 1000L;

    /** Never changing unique event type name. */
    public static final EventType TYPE = new EventType("PersonCreatedEvent");

    /** Unique name used for marshalling/unmarshalling the event. */
    public static final SerializedDataType SER_TYPE = new SerializedDataType(PersonCreatedEvent.TYPE.asBaseType());

    @NotNull
    @JsonbProperty("name")
    private PersonName name;

    /**
     * Protected default constructor for deserialization.
     */
    protected PersonCreatedEvent() {
        super();
    }

    /**
     * A new person was created in the system.
     *
     * @param id   Identifies uniquely a person.
     * @param name Name of a person.
     */
    public PersonCreatedEvent(@NotNull final PersonId id, @NotNull final PersonName name) {
        super(new EntityIdPath(id));
        Contract.requireArgNotNull("name", name);
        this.name = name;
    }

    @Override
    public final EventType getEventType() {
        return PersonCreatedEvent.TYPE;
    }

    /**
     * Returns: Name of a person.
     *
     * @return Current value.
     */
    @NotNull
    public final PersonName getName() {
        return name;
    }

    @Override
    public final String toString() {
        return "Person '" + name + "' (" + getEntityId() + ") was created [Event " + getEventId() + "]";
    }

}
