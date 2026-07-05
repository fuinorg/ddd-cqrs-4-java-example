package org.fuin.cqrs4j.example.quarkus.shared;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import org.fuin.cqrs4j.jsonb.JandexJsonbRegistry;
import org.fuin.cqrs4j.jsonb.JsonbRegistry;
import org.fuin.ddd4j.core.EntityIdFactory;
import org.fuin.ddd4j.core.JandexEntityIdFactory;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.api.SerializedDataTypeRegistry;
import org.fuin.esc.api.SimpleSerializerDeserializerRegistry;
import org.fuin.esc.client.JandexSerializedDataTypeRegistry;
import org.fuin.esc.jsonb.EscJsonbUtils;
import org.fuin.esc.jsonb.JsonbSerDeserializer;
import org.fuin.objects4j.jsonb.FieldAccessStrategy;
import org.fuin.objects4j.jsonb.JsonbProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * CDI factory that wires the esc JSON-B serialization stack (esc 0.10 API): a shared
 * {@link JsonbProvider}, the type/serializer registries, and the {@link SerDeserializerRegistry}
 * that the event store producers consume. Building the registry also registers the JSON-B
 * adapters/serializers/deserializers onto the shared {@link JsonbConfig} as a side effect.
 */
@ApplicationScoped
public class SerDeserializerRegistryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SerDeserializerRegistryFactory.class);

    @Produces
    @Singleton
    public EntityIdFactory entityIdFactory() {
        return new JandexEntityIdFactory(); // Scans classes
    }

    @Produces
    @Singleton
    public JsonbConfig jsonbConfig() {
        return new JsonbConfig()
                .withPropertyVisibilityStrategy(new FieldAccessStrategy())
                .withEncoding(StandardCharsets.UTF_8.name());
    }

    @Produces
    @Singleton
    public JsonbProvider jsonbProvider(final JsonbConfig jsonbConfig) {
        return new JsonbProvider(jsonbConfig);
    }

    @Produces
    @Singleton
    public SerializedDataTypeRegistry serializedDataTypeRegistry() {
        return new JandexSerializedDataTypeRegistry(); // Scans classes
    }

    @Produces
    @Singleton
    public JsonbSerDeserializer jsonbSerDeserializer(final JsonbProvider jsonbProvider,
                                                     final SerializedDataTypeRegistry typeRegistry) {
        return new JsonbSerDeserializer(jsonbProvider, typeRegistry, StandardCharsets.UTF_8);
    }

    @Produces
    @Singleton
    public SerDeserializerRegistry serDeserializerRegistry(final JsonbConfig jsonbConfig,
                                                           final JsonbProvider jsonbProvider,
                                                           final EntityIdFactory entityIdFactory,
                                                           final SerializedDataTypeRegistry typeRegistry,
                                                           final JsonbSerDeserializer jsonbSerDeserializer) {

        final SimpleSerializerDeserializerRegistry.Builder builder =
                new SimpleSerializerDeserializerRegistry.Builder(EscJsonbUtils.MIME_TYPE);
        for (final SerializedDataTypeRegistry.TypeClass tc : typeRegistry.findAll()) {
            builder.add(tc.type(), jsonbSerDeserializer);
            LOG.info("Registered type '{}' with serializer: {}", tc.type().asBaseType(),
                    jsonbSerDeserializer.getClass().getSimpleName());
        }

        final SerDeserializerRegistry registry = builder.build();

        EscJsonbUtils.addEscSerDeserializer(builder, jsonbSerDeserializer);

        // Registers the ddd/esc/app JSON-B adapters, serializers and deserializers onto the shared
        // JsonbConfig so the event store (and REST layer via the same provider) can (un)marshal events.
        final JsonbRegistry jsonbRegistry = new JandexJsonbRegistry(entityIdFactory, registry, registry, jsonbProvider);
        jsonbConfig.withAdapters(jsonbRegistry.getAdapters().toArray(new JsonbAdapter[0]));
        jsonbConfig.withSerializers(jsonbRegistry.getSerializers().toArray(new JsonbSerializer[0]));
        jsonbConfig.withDeserializers(jsonbRegistry.getDeserializers().toArray(new JsonbDeserializer[0]));

        return registry;
    }

}
