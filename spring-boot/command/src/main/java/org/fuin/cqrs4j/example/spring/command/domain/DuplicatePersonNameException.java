package org.fuin.cqrs4j.example.spring.command.domain;

import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.fuin.objects4j.common.ExceptionShortIdentifable;

/**
 * A name that should be unique does already exist.
 */
public final class DuplicatePersonNameException extends Exception implements ExceptionShortIdentifable {

    private static final long serialVersionUID = 1000L;

    private static final String SHORT_ID = "DUPLICATE_PERSON_NAME";

    private final PersonId personId;

    private final PersonName name;

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