package org.fuin.cqrs4j.example.quarkus.shared;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.fuin.esc.api.ProjectionAdminEventStore;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import io.kurrent.dbclient.KurrentDBClientSettings;
import io.kurrent.dbclient.KurrentDBProjectionManagementClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.esgrpc.GrpcProjectionAdminEventStore;
import org.fuin.esc.esgrpc.GrpcProjectionAdminEventStore;

/**
 * CDI factory that creates a {@link ProjectionAdminEventStore} instance.
 */
@ApplicationScoped
public class ProjectionAdminEventStoreFactory {

    @Produces
    @ApplicationScoped
    public ProjectionAdminEventStore getProjectionAdminEventStore(final Config config) {

        final KurrentDBClientSettings settings = KurrentDBClientSettings.builder()
                .addHost(config.getEventStoreHost(), config.getEventStoreHttpPort())
                .defaultCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .tls(false)
                .buildConnectionSettings();
        final KurrentDBProjectionManagementClient client = KurrentDBProjectionManagementClient.create(settings);
        return new GrpcProjectionAdminEventStore(client).open();

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
