package org.fuin.cqrs4j.example.javasecdi.cmd.domain;

import jakarta.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.objects4j.common.Contract;

/**
 * A name that should be unique does already exist.
 */
public final class DuplicatePersonNameException extends Exception {

    private static final long serialVersionUID = 1000L;

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
        Contract.requireArgNotNull("personId", personId);
        Contract.requireArgNotNull("name", name);
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

}