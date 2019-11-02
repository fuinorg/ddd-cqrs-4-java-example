package org.fuin.cqrs4j.example.javasecdi.qry.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.fuin.cqrs4j.EventDispatcher;
import org.fuin.cqrs4j.SimpleEventDispatcher;

/**
 * Create an {@link EventDispatcher}.
 */
@ApplicationScoped
public class QryEventDispatcherFactory {

    @Produces
    @ApplicationScoped
    public EventDispatcher createDispatcher(final PersonCreatedEventHandler createdHandler) {
        return new SimpleEventDispatcher(createdHandler);
    }

}
