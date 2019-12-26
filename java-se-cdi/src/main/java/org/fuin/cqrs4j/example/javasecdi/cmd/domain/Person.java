package org.fuin.cqrs4j.example.javasecdi.cmd.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.ddd4j.ddd.AbstractAggregateRoot;
import org.fuin.ddd4j.ddd.ApplyEvent;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.objects4j.common.Contract;

/**
 * A medical practitioner most likely also holder of an accredited academic degree.
 */
public class Person extends AbstractAggregateRoot<PersonId> implements Serializable {

    private static final long serialVersionUID = 1000L;

    @NotNull
    private PersonId id;

    @NotNull
    private PersonName name;

    /**
     * Default constructor that is mandatory for aggregate roots.
     */
    public Person() {
        super();
    }

    /**
     * Constructor with all data.
     * 
     * @param id
     *            Unique identifier of the person.
     * @param name
     *            Unique name of the person.
     * @param service
     *            Service required by the method.
     * 
     * @throws DuplicatePersonNameException
     *             The name already exists for another person.
     */
    public Person(@NotNull final PersonId id, @NotNull final PersonName name, final PersonService service) throws DuplicatePersonNameException {
        super();

        // VERIFY PRECONDITIONS
        Contract.requireArgNotNull("id", id);
        Contract.requireArgNotNull("name", name);

        // VERIFY BUSINESS RULES

        // Rule 1: The name of the person must be unique
        final PersonId otherId = service.loadPersonIdByName(name);
        if (otherId != null) {
            throw new DuplicatePersonNameException(otherId, name);
        }

        // CREATE EVENT
        apply(new PersonCreatedEvent(id, name));

    }

    @Override
    public PersonId getId() {
        return id;
    }

    @Override
    public EntityType getType() {
        return PersonId.TYPE;
    }

    @ApplyEvent
    public void applyEvent(final PersonCreatedEvent event) {
        this.id = event.getEntityId();
        this.name = event.getName();
    }

    /**
     * Service for the constructor.
     */
    public static interface PersonService {

        /**
         * Loads the person's identifier for a given name.
         * 
         * @param name
         *            Person's name.
         * 
         * @return Office identifier or {@literal null} if not found.
         */
        public PersonId loadPersonIdByName(@NotNull PersonName name);

    }

}
