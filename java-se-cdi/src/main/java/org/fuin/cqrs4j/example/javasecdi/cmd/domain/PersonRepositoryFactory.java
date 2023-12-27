package org.fuin.cqrs4j.example.javasecdi.cmd.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import org.fuin.esc.api.EventStore;

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
     * @return Dependent scope project repository.
     */
    @Produces
    @Dependent
    public PersonRepository create(final EventStore eventStore) {
        return new PersonRepository(eventStore);
    }

}
