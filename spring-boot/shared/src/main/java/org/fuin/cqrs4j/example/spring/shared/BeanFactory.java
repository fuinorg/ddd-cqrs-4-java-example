package org.fuin.cqrs4j.example.spring.shared;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.esc.eshttp.ESEnvelopeType;
import org.fuin.esc.eshttp.ESHttpEventStore;
import org.fuin.esc.eshttp.IESHttpEventStore;
import org.fuin.esc.esjc.ESJCEventStore;
import org.fuin.esc.esjc.IESJCEventStore;
import org.fuin.esc.spi.EnhancedMimeType;
import org.fuin.esc.spi.SerDeserializerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.github.msemys.esjc.EventStoreBuilder;

@Component
public class BeanFactory {

    /**
     * Creates a Jsonb instance.
     * 
     * @return Fully configured instance.
     */
    @Bean
    public Jsonb createJsonb() {
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        return JsonbBuilder.create(config);
    }

    /**
     * Creates a TCP based event store connection.
     * 
     * @param config
     *            Configuration to use.
     * 
     * @return New event store instance.
     */
    @Bean(destroyMethod = "shutdown")
    public com.github.msemys.esjc.EventStore getEventStore(final Config config) {
        return EventStoreBuilder.newBuilder().singleNodeAddress(config.getEventStoreHost(), config.getEventStoreTcpPort())
                .executor(Executors.newFixedThreadPool(10)).userCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .build();
    }

    /**
     * Creates an event store connection.
     * 
     * @param config
     *            Configuration to use.
     * 
     * @return New event store instance.
     */
    @Bean(destroyMethod = "close")
    public IESJCEventStore getESJCEventStore(final com.github.msemys.esjc.EventStore es) {

        final SerDeserializerRegistry registry = SharedUtils.createRegistry();

        final IESJCEventStore eventstore = new ESJCEventStore.Builder().eventStore(es).serDesRegistry(registry)
                .targetContentType(EnhancedMimeType.create("application", "json", StandardCharsets.UTF_8)).build();

        eventstore.open();
        return eventstore;

    }

    /**
     * Creates a HTTP based event store connection.
     * 
     * @param config
     *            Configuration to use.
     * 
     * @return New event store instance.
     */
    @Bean(destroyMethod = "close")
    public IESHttpEventStore getESHttpEventStore(final Config config) {
        final String url = config.getEventStoreProtocol() + "://" + config.getEventStoreHost() + ":" + config.getEventStoreHttpPort();
        try {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(config.getEventStoreUser(),
                    config.getEventStorePassword());
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            final SerDeserializerRegistry registry = SharedUtils.createRegistry();

            final ESHttpEventStore es = new ESHttpEventStore.Builder().threadFactory(Executors.defaultThreadFactory()).url(new URL(url))
                    .envelopeType(ESEnvelopeType.JSON).serDesRegistry(registry).credentialsProvider(credentialsProvider).build();

            es.open();
            return es;
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Failed to create URL: " + url, ex);
        }
    }

}
