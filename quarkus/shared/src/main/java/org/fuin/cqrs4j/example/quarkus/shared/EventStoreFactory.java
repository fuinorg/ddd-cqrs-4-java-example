package org.fuin.cqrs4j.example.quarkus.shared;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.fuin.esc.esgrpc.ESGrpcEventStore;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.fuin.esc.spi.EnhancedMimeType;
import org.fuin.esc.spi.SerDeserializerRegistry;

import java.nio.charset.StandardCharsets;

/**
 * CDI factory that creates an event store connection.
 */
@ApplicationScoped
public class EventStoreFactory {

    /**
     * Creates an GRPC based event store.<br>
     * <br>
     * CAUTION: The returned event store instance is NOT thread safe.
     *
     * @param config
     *            Configuration to use.
     * @param registry
     *            Serialization registry.
     *
     * @return Application scope event store.
     */
    @Produces
    @ApplicationScoped
    public IESGrpcEventStore createEventStore(final Config config, final SerDeserializerRegistry registry) {

        final EventStoreDBClientSettings setts = EventStoreDBClientSettings.builder()
                .addHost(config.getEventStoreHost(), config.getEventStoreHttpPort())
                .defaultCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .tls(false)
                .buildConnectionSettings();

        final EventStoreDBClient client = EventStoreDBClient.create(setts);
        final IESGrpcEventStore eventstore = new ESGrpcEventStore.Builder().eventStore(client).serDesRegistry(registry)
                .targetContentType(EnhancedMimeType.create("application", "json", StandardCharsets.UTF_8))
                .build();

        eventstore.open();
        return eventstore;

    }

    /**
     * Closes the GRPC based event store when the context is disposed.
     *
     * @param es
     *            Event store to close.
     */
    public void closeEventStore(@Disposes final IESGrpcEventStore es) {
        es.close();
    }

}
