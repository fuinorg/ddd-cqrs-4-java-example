package org.fuin.cqrs4j.example.aggregates;

import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.ddd4j.ddd.AbstractAggregateRoot;
import org.fuin.ddd4j.ddd.AggregateDeletedException;
import org.fuin.ddd4j.ddd.ApplyEvent;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.objects4j.common.Contract;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents a natural person.
 */
public class Person extends AbstractAggregateRoot<PersonId> implements Serializable {

    private static final long serialVersionUID = 1000L;

    @NotNull
    private PersonId id;

    @NotNull
    private PersonName name;

    private boolean deleted;

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
    public Person(@NotNull final PersonId id, @NotNull final PersonName name, final CreatePersonService service)
            throws DuplicatePersonNameException {
        super();

        // VERIFY PRECONDITIONS
        Contract.requireArgNotNull("id", id);
        Contract.requireArgNotNull("name", name);

        // VERIFY BUSINESS RULES

        // Rule 1: The name of the person must be unique
        final Optional<PersonId> otherId = service.loadPersonIdByName(name);
        if (otherId.isPresent()) {
            throw new DuplicatePersonNameException(otherId.get(), name);
        }

        // CREATE EVENT
        apply(new PersonCreatedEvent.Builder().id(id).name(name).version(getNextVersion() + 1).build());

    }

    /**
     * Deletes the person.
     *
     * @throws AggregateDeletedException The aggregate was already deleted.
     */
    public void delete() throws AggregateDeletedException {
        if (deleted) {
            throw new AggregateDeletedException(PersonId.TYPE, id);
        }
        apply(new PersonDeletedEvent.Builder().id(id).name(name).version(getNextVersion() + 1).build());
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

    @ApplyEvent
    public void applyEvent(final PersonDeletedEvent event) {
        this.deleted = true;
    }

    /**
     * Service for the constructor.
     */
    public static interface CreatePersonService {

        /**
         * Loads the person's identifier for a given name.
         * 
         * @param name
         *            Person's name.
         * 
         * @return Office identifier or empty if not found.
         */
        public Optional<PersonId> loadPersonIdByName(@NotNull PersonName name);

    }

}
