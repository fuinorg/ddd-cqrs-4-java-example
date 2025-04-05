package org.fuin.cqrs4j.example.spring.shared;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.esc.api.EnhancedMimeType;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.esgrpc.ESGrpcEventStore;
import org.fuin.esc.esgrpc.GrpcProjectionAdminEventStore;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.fuin.esc.jsonb.BaseTypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.KurrentDBClientSettings;
import io.kurrent.dbclient.KurrentDBProjectionManagementClient;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

@Component
public class BeanFactory {

    /**
     * Creates a Jsonb instance.
     *
     * @return Fully configured instance.
     */
    @Bean
    public Jsonb createJsonb() {
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        return JsonbBuilder.create(config);
    }


    @Bean
    public KurrentDBClient createKurrentDBClient(final Config config) {
        final KurrentDBClientSettings settings = KurrentDBClientSettings.builder()
                .addHost(config.getEventStoreHost(), config.getEventStoreHttpPort())
                .defaultCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .tls(false)
                .buildConnectionSettings();
        return KurrentDBClient.create(settings);
    }

    @Bean
    public KurrentDBProjectionManagementClient createKurrentDBProjectionManagementClient(final Config config) {
        final KurrentDBClientSettings settings = KurrentDBClientSettings.builder()
                .addHost(config.getEventStoreHost(), config.getEventStoreHttpPort())
                .defaultCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .tls(false)
                .buildConnectionSettings();
        return KurrentDBProjectionManagementClient.create(settings);
    }


    /**
     * Creates an event store connection.
     *
     * @param client Client to use.
     * @return New event store instance.
     */
    @Bean(destroyMethod = "close")
    public IESGrpcEventStore getESGrpcEventStore(final KurrentDBClient client) {

        final SerDeserializerRegistry registry = SharedUtils.createRegistry();

        return new ESGrpcEventStore.Builder()
        		.eventStore(client)
        		.serDesRegistry(registry)
        		.baseTypeFactory(new BaseTypeFactory())
                .targetContentType(EnhancedMimeType.create("application", "json", StandardCharsets.UTF_8))
                .build()
                .open();

    }

    @Bean
    public HttpClient getHttpClient(final Config config) {
        return HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getEventStoreUser(), config.getEventStorePassword().toCharArray());
                    }
                })
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
    }


    /**
     * Creates an GRPC based projection admin event store.
     *
     * @param client Client to use.
     * @return New event store instance.
     */
    @Bean(destroyMethod = "close")
    public ProjectionAdminEventStore getProjectionAdminEventStore(final KurrentDBProjectionManagementClient client) {
        return new GrpcProjectionAdminEventStore(client).open();
    }

}
