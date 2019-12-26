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

import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.json.bind.adapter.JsonbAdapter;
import javax.validation.constraints.NotNull;

import org.fuin.ddd4j.ddd.AggregateRootUuid;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.ddd4j.ddd.StringBasedEntityType;
import org.fuin.objects4j.ui.Label;
import org.fuin.objects4j.ui.ShortLabel;
import org.fuin.objects4j.ui.Tooltip;
import org.fuin.objects4j.vo.ValueObjectConverter;

/**
 * Identifies uniquely a person.
 */
@ShortLabel(bundle = "ddd-cqrs-4-java-example", key = "PersonId.slabel", value = "PID")
@Label(bundle = "ddd-cqrs-4-java-example", key = "PersonId.label", value = "Person's ID")
@Tooltip(bundle = "ddd-cqrs-4-java-example", key = "PersonId.tooltip", value = "Unique identifier of a person")
@Immutable
public final class PersonId extends AggregateRootUuid {

    private static final long serialVersionUID = 1000L;

    /** Unique name of the aggregate this identifier refers to. */
    public static final EntityType TYPE = new StringBasedEntityType("PERSON");

    /**
     * Default constructor.
     */
    protected PersonId() {
        super(PersonId.TYPE);
    }

    /**
     * Constructor with all data.
     *
     * @param value
     *            Persistent value.
     */
    public PersonId(@NotNull final UUID value) {
        super(PersonId.TYPE, value);
    }

    /**
     * Verifies if the given string can be converted into a Person ID.
     * 
     * @param value
     *            String with valid UUID string. A <code>null</code> value ris also valid.
     * 
     * @return {@literal true} if the string is a valid UUID.
     */
    public static boolean isValid(final String value) {
        if (value == null) {
            return true;
        }
        return AggregateRootUuid.isValid(value);
    }

    /**
     * Parses a given string and returns a new instance of PersonId.
     * 
     * @param value
     *            String with valid UUID to convert. A <code>null</code> value returns <code>null</code>.
     * 
     * @return Converted value.
     */
    public static PersonId valueOf(final String value) {
        if (value == null) {
            return null;
        }
        AggregateRootUuid.requireArgValid("value", value);
        return new PersonId(UUID.fromString(value));
    }

    /**
     * Converts the value object from/to UUID.
     */
    public static final class Converter implements ValueObjectConverter<UUID, PersonId>, JsonbAdapter<PersonId, UUID> {

        // Attribute Converter

        @Override
        public final Class<UUID> getBaseTypeClass() {
            return UUID.class;
        }

        @Override
        public final Class<PersonId> getValueObjectClass() {
            return PersonId.class;
        }

        @Override
        public boolean isValid(final UUID value) {
            return true;
        }

        @Override
        public final PersonId toVO(final UUID value) {
            if (value == null) {
                return null;
            }
            return new PersonId(value);
        }

        @Override
        public final UUID fromVO(final PersonId value) {
            if (value == null) {
                return null;
            }
            return value.asBaseType();
        }

        // JSONB Adapter

        @Override
        public final UUID adaptToJson(final PersonId obj) throws Exception {
            return fromVO(obj);
        }

        @Override
        public final PersonId adaptFromJson(final UUID value) throws Exception {
            return toVO(value);
        }

    }

}