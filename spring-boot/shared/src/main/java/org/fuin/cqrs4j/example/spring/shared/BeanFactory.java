package org.fuin.cqrs4j.example.spring.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.KurrentDBClientSettings;
import io.kurrent.dbclient.KurrentDBProjectionManagementClient;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.cqrs4j.jackson.Cqrs4JacksonAdapterModule;
import org.fuin.ddd4j.core.EntityIdFactory;
import org.fuin.ddd4j.core.JandexEntityIdFactory;
import org.fuin.ddd4j.jackson.Ddd4JacksonAdapterModule;
import org.fuin.esc.api.EnhancedMimeType;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.esgrpc.ESGrpcEventStore;
import org.fuin.esc.esgrpc.GrpcProjectionAdminEventStore;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.fuin.esc.jackson.BaseTypeFactory;
import org.fuin.objects4j.jackson.Objects4JJacksonAdapterModule;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class BeanFactory {

    @Bean
    public EntityIdFactory entityIdFactory() {
        return new JandexEntityIdFactory(); // Scans classes
    }

    @Bean
    public ObjectMapper objectMapper(EntityIdFactory entityIdFactory) {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new ExampleJacksonAdapterModule(entityIdFactory))
                .registerModule(new Objects4JJacksonAdapterModule())
                //.registerModule(new EscJacksonAdapterModule())
                .registerModule(new Ddd4JacksonAdapterModule(entityIdFactory))
                .registerModule(new Cqrs4JacksonAdapterModule());
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
