package org.fuin.cqrs4j.example.shared;

import com.google.errorprone.annotations.Immutable;

import jakarta.annotation.Generated;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import org.fuin.ddd4j.ddd.AggregateRootUuid;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.ddd4j.ddd.HasEntityTypeConstant;
import org.fuin.ddd4j.ddd.StringBasedEntityType;
import org.fuin.objects4j.common.ConstraintViolationException;
import org.fuin.objects4j.common.HasPublicStaticValueOfMethod;
import org.fuin.objects4j.vo.ValueObjectConverter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

/** Unique identifier of a customer. */
@Generated("Generated class - Manual changes will be overwritten")
@Immutable
@HasPublicStaticValueOfMethod
@HasEntityTypeConstant
public final class CustomerId extends AggregateRootUuid {

    private static final long serialVersionUID = 1L;

    /** Unique name of the aggregate this identifier refers to. */
    public static final EntityType TYPE = new StringBasedEntityType("CUSTOMER");

    /** Default constructor that generates a random UUID. */
    public CustomerId() {
        super(TYPE);
    }

    /**
     * Constructor with mandatory data.
     *
     * @param value Value.
     */
    public CustomerId(final UUID value) {
        super(TYPE, value);
    }

    /**
     * Parses a given string and returns a new instance of this type.
     *
     * @param value String with valid UUID to convert. A <code>null</code> value returns <code>null
     *     </code>.
     * @return Converted value.
     */
    public static CustomerId valueOf(final String value) {
        if (value == null) {
            return null;
        }
        requireArgValid("value", value);
        return new CustomerId(UUID.fromString(value));
    }

    /**
     * Verifies that a given string can be converted into the type.
     *
     * @param value Value to validate.
     * @return Returns <code>true</code> if it's a valid type else <code>false</code>.
     */
    public static boolean isValid(final String value) {
        if (value == null) {
            return true;
        }
        return AggregateRootUuid.isValid(value);
    }

    /**
     * Verifies if the argument is valid and throws an exception if this is not the case.
     *
     * @param name Name of the value for a possible error message.
     * @param value Value to check.
     * @throws ConstraintViolationException The value was not valid.
     */
    public static void requireArgValid(@NotNull final String name, @NotNull final String value)
            throws ConstraintViolationException {
        if (!isValid(value)) {
            throw new ConstraintViolationException(
                    "The argument '" + name + "' is not valid: '" + value + "'");
        }
    }

    /** Ensures that the string can be converted into the type. */
    @Target({
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE
    })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {Validator.class})
    @Documented
    public static @interface CustomerIdStr {

        String message() default "{org.fuin.cqrs4j.example.shared.CustomerId.message}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    /** Validates if a string is compliant with the type. */
    public static final class Validator implements ConstraintValidator<CustomerIdStr, String> {

        @Override
        public final void initialize(final CustomerIdStr annotation) {
            // Not used
        }

        @Override
        public final boolean isValid(final String value, final ConstraintValidatorContext context) {
            return CustomerId.isValid(value);
        }
    }

    /** Converts the value object from/to string. */
    public static final class Converter
            implements ValueObjectConverter<UUID, CustomerId>, JsonbAdapter<CustomerId, UUID> {

        // Attribute Converter

        @Override
        public final Class<UUID> getBaseTypeClass() {
            return UUID.class;
        }

        @Override
        public final Class<CustomerId> getValueObjectClass() {
            return CustomerId.class;
        }

        @Override
        public boolean isValid(final UUID value) {
            return true;
        }

        @Override
        public final CustomerId toVO(final UUID value) {
            if (value == null) {
                return null;
            }
            return new CustomerId(value);
        }

        @Override
        public final UUID fromVO(final CustomerId value) {
            if (value == null) {
                return null;
            }
            return value.asBaseType();
        }

        // JSONB Adapter

        @Override
        public final UUID adaptToJson(final CustomerId obj) throws Exception {
            return fromVO(obj);
        }

        @Override
        public final CustomerId adaptFromJson(final UUID str) throws Exception {
            return toVO(str);
        }
    }
}
