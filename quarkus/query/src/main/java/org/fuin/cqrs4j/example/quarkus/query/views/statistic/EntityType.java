package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

/**
 * Defines the name of a type of entity.
 *
 * @param name Unique name. Will be converted to lowercase internally - Minimum 1 character, maximum 30 characters.
 */
public record EntityType(String name) {

    /**
     * Maximum allowed length of the name.
     */
    public static final int MAX_LENGTH = 30;

    public EntityType(@NotEmpty @Max(MAX_LENGTH) String name) {
        this.name = Objects.requireNonNull(name, "name==null").toLowerCase();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Name has a length of " + name.length()
                    + ", but max allowed is " + MAX_LENGTH + " characters: '" + name + "'");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Determines if the name is valid.
     *
     * @param name Name to be verified.
     * @return {@literal true} if the given name can be converted into an instance of this class.
     */
    public static boolean isValid(String name) {
        return name != null && !name.isEmpty() && name.length() <= MAX_LENGTH;
    }


}
