package org.fuin.cqrs4j.example.spring.shared;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.KurrentDBClientSettings;
import io.kurrent.dbclient.KurrentDBProjectionManagementClient;
import org.fuin.cqrs4j.jackson.Cqrs4JacksonModule;
import org.fuin.cqrs4j.springboot.base.EventstoreConfig;
import org.fuin.ddd4j.core.EntityIdFactory;
import org.fuin.ddd4j.core.JandexEntityIdFactory;
import org.fuin.ddd4j.jackson.Ddd4JacksonModule;
import org.fuin.esc.api.EnhancedMimeType;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.api.SerializedDataTypeRegistry;
import org.fuin.esc.api.SimpleSerializerDeserializerRegistry;
import org.fuin.esc.client.JandexSerializedDataTypeRegistry;
import org.fuin.esc.esgrpc.ESGrpcEventStore;
import org.fuin.esc.esgrpc.GrpcProjectionAdminEventStore;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.fuin.esc.jackson.BaseTypeFactory;
import org.fuin.esc.jackson.EscJacksonModule;
import org.fuin.esc.jackson.EscJacksonUtils;
import org.fuin.esc.jackson.JacksonSerDeserializer;
import org.fuin.objects4j.jackson.ImmutableObjectMapper;
import org.fuin.objects4j.jackson.Objects4JJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class SharedConfig {

    @Bean
    public EntityIdFactory entityIdFactory() {
        return new JandexEntityIdFactory(); // Scans classes
    }

    @Bean
    public ImmutableObjectMapper.Builder immutableObjectMapperBuilder(
            EntityIdFactory entityIdFactory) {
        return new ImmutableObjectMapper.Builder(new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .registerModule(new SharedJacksonModule(entityIdFactory))
                .registerModule(new Cqrs4JacksonModule())
                .registerModule(new Objects4JJacksonModule())
                .registerModule(new Ddd4JacksonModule(entityIdFactory))
        );
    }

    @Bean
    public ImmutableObjectMapper.Provider immutableObjectMapperProvider(
            ImmutableObjectMapper.Builder mapperBuilder) {
        return new ImmutableObjectMapper.Provider(mapperBuilder);
    }

    @Bean
    public JacksonSerDeserializer jacksonSerDeserializer(
            final ImmutableObjectMapper.Provider mapperProvider,
            final SerializedDataTypeRegistry typeRegistry) {
        return new JacksonSerDeserializer.Builder()
                .withObjectMapper(mapperProvider)
                .withTypeRegistry(typeRegistry)
                .withEncoding(StandardCharsets.UTF_8)
                .build();
    }


    @Bean
    public SerializedDataTypeRegistry serializedDataTypeRegistry() {
        return new JandexSerializedDataTypeRegistry();
    }

    @Bean
    public SerDeserializerRegistry serDeserializerRegistry(SerializedDataTypeRegistry typeRegistry,
                                                           JacksonSerDeserializer jacksonSerDeserializer,
                                                           ImmutableObjectMapper.Builder mapperBuilder) {

        final SimpleSerializerDeserializerRegistry.Builder builder = new SimpleSerializerDeserializerRegistry.Builder(EscJacksonUtils.MIME_TYPE);
        for (final SerializedDataTypeRegistry.TypeClass tc : typeRegistry.findAll()) {
            builder.add(tc.type(), jacksonSerDeserializer);
        }
        final SerDeserializerRegistry registry = builder.build();
        mapperBuilder.registerModule(new EscJacksonModule(registry, registry));
        return registry;
    }

    @Bean
    @Primary
    @DependsOn("serDeserializerRegistry")
    public ObjectMapper objectMapper(final ImmutableObjectMapper.Provider mapperProvider) {
        return mapperProvider.mapper().objectMapper();
    }

    @Bean(destroyMethod = "shutdown")
    public KurrentDBClient createKurrentDBClient(final EventstoreConfig config) {
        final KurrentDBClientSettings settings = KurrentDBClientSettings.builder()
                .addHost(config.getHost(), config.getPort())
                .defaultCredentials("admin", "changeit") // Just for test
                .tls(config.isTls())
                .buildConnectionSettings();
        return KurrentDBClient.create(settings);
    }

    @Bean
    public KurrentDBProjectionManagementClient createKurrentDBProjectionManagementClient(final EventstoreConfig config) {
        final KurrentDBClientSettings settings = KurrentDBClientSettings.builder()
                .addHost(config.getHost(), config.getPort())
                .defaultCredentials("admin", "changeit") // Just for test
                .tls(config.isTls())
                .buildConnectionSettings();
        return KurrentDBProjectionManagementClient.create(settings);
    }


    /**
     * Creates an event store connection.
     *
     * @param client Client to use.
     * @return New event store instance.
     */
    @SuppressWarnings("java:S2095") // Spring will correctly close it by calling "close()" on instance
    @Bean(destroyMethod = "close")
    public IESGrpcEventStore getESGrpcEventStore(final SerDeserializerRegistry registry,
                                                 final KurrentDBClient client) {
        return new ESGrpcEventStore.Builder()
                .eventStore(client)
                .serDesRegistry(registry)
                .baseTypeFactory(new BaseTypeFactory())
                .targetContentType(EnhancedMimeType.create("application", "json", StandardCharsets.UTF_8))
                .build()
                .open();
    }

    /**
     * Creates an GRPC based projection admin event store.
     *
     * @param client Client to use.
     * @return New event store instance.
     */
    @SuppressWarnings("java:S2095") // Spring will correctly close it by calling "close()" on instance
    @Bean(destroyMethod = "close")
    public ProjectionAdminEventStore getProjectionAdminEventStore(final KurrentDBProjectionManagementClient client) {
        return new GrpcProjectionAdminEventStore(client).open();
    }

    @Bean
    public HttpClient getHttpClient(final EventstoreConfig config) {
        return HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        // Just for test
                        return new PasswordAuthentication(
                                "admin",
                                "changeit".toCharArray());
                    }
                })
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
    }

}
