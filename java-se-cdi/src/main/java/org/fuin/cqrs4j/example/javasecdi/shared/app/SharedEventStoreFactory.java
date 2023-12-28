package org.fuin.cqrs4j.example.javasecdi.shared.app;

import java.nio.charset.Charset;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

import org.fuin.esc.api.EventStore;
import org.fuin.esc.esjc.ESJCEventStore;
import org.fuin.esc.spi.EnhancedMimeType;
import org.fuin.esc.spi.SerDeserializerRegistry;

/**
 * CDI factory that creates an event store connection and repositories.
 */
@ApplicationScoped
public class SharedEventStoreFactory {

    /**
     * Creates an event store.<br>
     * <br>
     * CAUTION: The returned event store instance is NOT thread safe.
     * 
     * @param es
     *            Native event store API.
     * @param registry
     *            Serialization registry.
     * 
     * @return Dependent scope event store.
     */
    @Produces
    @RequestScoped
    public EventStore createEventStore(final com.github.msemys.esjc.EventStore es, final SerDeserializerRegistry registry) {

        final EventStore eventstore = new ESJCEventStore.Builder().eventStore(es).serDesRegistry(registry)
                .targetContentType(EnhancedMimeType.create("application", "json", Charset.forName("utf-8"))).build();

        eventstore.open();
        return eventstore;

    }

    /**
     * Closes the event store when the context is disposed.
     * 
     * @param es
     *            Event store to close.
     */
    public void closeEventStore(@Disposes final EventStore es) {
        es.close();
    }

}
