package org.fuin.cqrs4j.example.javasecdi.qry.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.fuin.cqrs4j.example.javasecdi.shared.app.SharedConfig;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.eshttp.ESEnvelopeType;
import org.fuin.esc.eshttp.ESHttpEventStore;
import org.fuin.esc.spi.SimpleSerializerDeserializerRegistry;

/**
 * CDI producer that creates an {@link ProjectionAdminEventStore}.
 */
@ApplicationScoped
public class QryProjectionAdminEventStoreFactory {

    /**
     * Creates an event store that is capable of administering projections.
     * 
     * @return Dependent scope projection admin event store.
     */
    @Produces
    @Dependent
    public ProjectionAdminEventStore createProjectionAdminEventStore(final SharedConfig config, final ThreadFactory threadFactory) {
        final String url = "http://" + config.getEventStoreHost() + ":" + config.getEventStoreHttpPort();
        try {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(config.getEventStoreUser(),
                    config.getEventStorePassword());
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            final SimpleSerializerDeserializerRegistry registry = new SimpleSerializerDeserializerRegistry();
            final ProjectionAdminEventStore es = new ESHttpEventStore(threadFactory, new URL(url), ESEnvelopeType.JSON, registry, registry,
                    credentialsProvider);
            es.open();
            return es;
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Failed to create URL: " + url, ex);
        }
    }

    /**
     * Closes the projection admin event store when the context is disposed.
     * 
     * @param es
     *            Projection admin event store to close.
     */
    public void closeProjectionAdminEventStore(@Disposes ProjectionAdminEventStore es) {
        es.close();
    }

}
