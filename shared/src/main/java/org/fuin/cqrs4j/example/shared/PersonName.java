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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.concurrent.Immutable;
import javax.json.bind.adapter.JsonbAdapter;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

import org.fuin.objects4j.common.ConstraintViolationException;
import org.fuin.objects4j.ui.Label;
import org.fuin.objects4j.ui.ShortLabel;
import org.fuin.objects4j.ui.Tooltip;
import org.fuin.objects4j.vo.AbstractStringValueObject;
import org.fuin.objects4j.vo.ValueObjectConverter;

/**
 * Name of a person.
 */
@ShortLabel(bundle = "ddd-cqrs-4-java-example", key = "PersonName.slabel", value = "PNAME")
@Label(bundle = "ddd-cqrs-4-java-example", key = "PersonName.label", value = "Person's name")
@Tooltip(bundle = "ddd-cqrs-4-java-example", key = "PersonName.tooltip", value = "Name of a person")
@Immutable
public final class PersonName extends AbstractStringValueObject {

    private static final long serialVersionUID = 1000L;

    /** Max length of a person's name. */
    public static final int MAX_LENGTH = 100;

    @NotNull
    @PersonNameStr
    private String value;

    /**
     * Protected default constructor for deserialization.
     */
    protected PersonName() {
        super();
    }

    /**
     * Constructor with mandatory data.
     * 
     * @param value Value.
     */
    public PersonName(final String value) {
        super();
        PersonName.requireArgValid("value", value);
        this.value = value;
    }

    @Override
    public final String asBaseType() {
        return value;
    }

    @Override
    public final String toString() {
        return value;
    }
    
    /**
     * Verifies that a given string can be converted into the type.
     * 
     * @param value Value to validate.
     * 
     * @return Returns <code>true</code> if it's a valid type else
     *         <code>false</code>.
     */
    public static boolean isValid(final String value) {
        if (value == null) {
            return true;
        }
        if (value.length() == 0) {
            return false;
        }
        final String trimmed = value.trim();
        if (trimmed.length() > PersonName.MAX_LENGTH) {
            return false;
        }
        return true;
    }

    /**
     * Verifies if the argument is valid and throws an exception if this is not the
     * case.
     * 
     * @param name  Name of the value for a possible error message.
     * @param value Value to check.
     * 
     * @throws ConstraintViolationException The value was not valid.
     */
    public static void requireArgValid(@NotNull final String name, @NotNull final String value)
            throws ConstraintViolationException {

        if (!PersonName.isValid(value)) {
            throw new ConstraintViolationException("The argument '" + name + "' is not valid: '" + value + "'");
        }

    }

    /**
     * Ensures that the string can be converted into the type.
     */
    @Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = { Validator.class })
    @Documented
    public static @interface PersonNameStr {

        String message()

        default "{org.fuin.cqrs4j.example.javasecdi.PersonName.message}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    /**
     * Validates if a string is compliant with the type.
     */
    public static final class Validator implements ConstraintValidator<PersonNameStr, String> {

        @Override
        public final void initialize(final PersonNameStr annotation) {
            // Not used
        }

        @Override
        public final boolean isValid(final String value, final ConstraintValidatorContext context) {
            return PersonName.isValid(value);
        }

    }

    /**
     * Converts the value object from/to string.
     */
    public static final class Converter
            implements ValueObjectConverter<String, PersonName>, JsonbAdapter<PersonName, String> {

        // Attribute Converter

        @Override
        public final Class<String> getBaseTypeClass() {
            return String.class;
        }

        @Override
        public final Class<PersonName> getValueObjectClass() {
            return PersonName.class;
        }

        @Override
        public boolean isValid(final String value) {
            return PersonName.isValid(value);
        }

        @Override
        public final PersonName toVO(final String value) {
            if (value == null) {
                return null;
            }
            return new PersonName(value);
        }

        @Override
        public final String fromVO(final PersonName value) {
            if (value == null) {
                return null;
            }
            return value.asBaseType();
        }

        // JSONB Adapter

        @Override
        public final String adaptToJson(final PersonName obj) throws Exception {
            return fromVO(obj);
        }

        @Override
        public final PersonName adaptFromJson(final String str) throws Exception {
            return toVO(str);
        }

    }

}
