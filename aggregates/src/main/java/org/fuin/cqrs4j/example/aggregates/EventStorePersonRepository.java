package org.fuin.cqrs4j.example.aggregates;

import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.ddd.EntityType;
import org.fuin.ddd4j.esrepo.EventStoreRepository;
import org.fuin.esc.api.EventStore;
import org.fuin.objects4j.common.NotThreadSafe;

/**
 * Event sourced repository for storing a {@link Person} aggregate.
 */
@NotThreadSafe
public class EventStorePersonRepository extends EventStoreRepository<PersonId, Person> implements PersonRepository {

    /**
     * Constructor all mandatory data.
     *
     * @param eventStore
     *            Event store.
     */
    public EventStorePersonRepository(final EventStore eventStore) {
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