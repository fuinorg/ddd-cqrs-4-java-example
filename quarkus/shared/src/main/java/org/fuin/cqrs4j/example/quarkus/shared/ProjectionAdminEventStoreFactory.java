package org.fuin.cqrs4j.example.quarkus.shared;

<<<<<<< ours
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.fuin.esc.admin.HttpProjectionAdminEventStore;
import org.fuin.esc.api.ProjectionAdminEventStore;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
=======
import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBProjectionManagementClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.esgrpc.GrpcProjectionAdminEventStore;
>>>>>>> theirs

/**
 * CDI factory that creates a {@link ProjectionAdminEventStore} instance.
 */
@ApplicationScoped
public class ProjectionAdminEventStoreFactory {

    @Produces
    @ApplicationScoped
<<<<<<< ours
    public ProjectionAdminEventStore getProjectionAdminEventStore(final Config config, final HttpClient httpClient) {
        final String url = config.getEventStoreProtocol() + "://" + config.getEventStoreHost() + ":" + config.getEventStoreHttpPort();
        try {
            final ProjectionAdminEventStore es = new HttpProjectionAdminEventStore(httpClient, new URL(url));
            es.open();
            return es;
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Failed to create URL: " + url, ex);
        }
=======
    public ProjectionAdminEventStore getProjectionAdminEventStore(final Config config) {

        final EventStoreDBClientSettings settings = EventStoreDBClientSettings.builder()
                .addHost(config.getEventStoreHost(), config.getEventStoreHttpPort())
                .defaultCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .tls(false)
                .buildConnectionSettings();
        final EventStoreDBProjectionManagementClient client = EventStoreDBProjectionManagementClient.create(settings);
        return new GrpcProjectionAdminEventStore(client).open();

>>>>>>> theirs
    }

    /**
     * Closes the projection admin event store when the context is disposed.
     *
     * @param es Event store to close.
     */
    public void closeProjectionAdminEventStore(@Disposes final ProjectionAdminEventStore es) {
        es.close();
    }

}
