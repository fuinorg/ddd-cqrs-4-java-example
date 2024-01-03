package org.fuin.cqrs4j.example.quarkus.command.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import org.fuin.cqrs4j.example.aggregates.PersonRepository;
import org.fuin.esc.esgrpc.IESGrpcEventStore;

/**
 * CDI factory that creates an event store connection and repositories.
 */
@ApplicationScoped
public class PersonRepositoryFactory {

    /**
     * Creates a repository.
     * 
     * @param eventStore
     *            Event store implementation.
     * 
     * @return Request scoped project repository.
     */
    @Produces
    @Dependent
    public PersonRepository create(final IESGrpcEventStore eventStore) {
        return new EventStorePersonRepository(eventStore);
    }

}
