package org.fuin.cqrs4j.example.javasecdi.cmd.domain;

import org.fuin.objects4j.common.NotThreadSafe;
import jakarta.validation.constraints.NotNull;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.ddd4j.esrepo.EventStoreRepository;
import org.fuin.esc.api.EventStore;

/**
 * Event sourced repository for storing a {@link Person} aggregate.
 */
@NotThreadSafe
public class PersonRepository extends EventStoreRepository<PersonId, Person> {

    /**
     * Constructor all mandatory data.
     * 
     * @param eventStore
     *            Event store.
     */
    public PersonRepository(final EventStore eventStore) {
        super(eventStore);
    }

    @Override
    @NotNull
    public Class<Person> getAggregateClass() {
        return Person.class;
    }

    @Override
    @NotNull
    public EntityType getAggregateType() {
        return PersonId.TYPE;
    }

    @Override
    @NotNull
    public Person create() {
        return new Person();
    }

    @Override
    @NotNull
    public String getIdParamName() {
        return "personId";
    }

}