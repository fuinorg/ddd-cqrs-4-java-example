package org.fuin.cqrs4j.example.shared;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.validation.constraints.NotNull;
import org.fuin.ddd4j.core.EntityIdFactory;
import org.fuin.ddd4j.core.JandexEntityIdFactory;
import org.fuin.ddd4j.jsonb.AggregateVersionJsonbAdapter;
import org.fuin.ddd4j.jsonb.EntityIdJsonbAdapter;
import org.fuin.ddd4j.jsonb.EntityIdPathJsonbAdapter;
import org.fuin.ddd4j.jsonb.EventIdJsonbAdapter;
import org.fuin.esc.api.IBase64Data;
import org.fuin.esc.api.IEscEvent;
import org.fuin.esc.api.IEscEvents;
import org.fuin.esc.api.IEscMeta;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.api.SerializedDataType;
import org.fuin.esc.api.SerializedDataTypeRegistry;
import org.fuin.esc.api.SimpleSerializerDeserializerRegistry;
import org.fuin.esc.client.JandexSerializedDataTypeRegistry;
import org.fuin.esc.jsonb.EscJsonbUtils;
import org.fuin.esc.jsonb.JsonbDeSerializer;
import org.fuin.objects4j.jsonb.FieldAccessStrategy;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Utility code shared between command (write) and query (read) module.
 */
public final class SharedUtils {

    private static final String APPLICATION_JSON = "application/json";

    /** All types that will be written into and read from the event store. */
    private static final SerializedDataType[] USER_DEFINED_TYPES = new SerializedDataType[] {
            PersonCreatedEvent.SER_TYPE,
            PersonDeletedEvent.SER_TYPE
    };

    /** All JSON-B adapters from this module. */
    private static List<JsonbAdapter<?, ?>> JSONB_ADAPTERS;

    private SharedUtils() {
        throw new UnsupportedOperationException("It is not allowed to create an instance of a utiliy class");
    }

    /**
     * Returns all JSON-B adapter.
     *
     * @return JSON-B adapter list.
     */
    public static synchronized JsonbAdapter<?, ?>[] getJsonbAdapters() {
        if (JSONB_ADAPTERS == null) {
            final EntityIdFactory entityIdFactory = new JandexEntityIdFactory(); // Scans classes
            JSONB_ADAPTERS = List.of(
                    new EventIdJsonbAdapter(),
                    new EntityIdPathJsonbAdapter(entityIdFactory),
                    new EntityIdJsonbAdapter(entityIdFactory),
                    new AggregateVersionJsonbAdapter(),
                    new PersonId.PersonIdJsonbAdapter(),
                    new PersonName.PersonNameJsonbAdapter()
            );
        }
        return JSONB_ADAPTERS.toArray(new JsonbAdapter<?, ?>[0]);
    }


    /**
     * Create a registry that allows finding types (classes) based on their unique type name.
     * 
     * @return New instance.
     */
    @NotNull
    public static SerializedDataTypeRegistry createTypeRegistry() {
        return new JandexSerializedDataTypeRegistry(); // Scans classes
    }

    /**
     * Creates a registry that connects the type with the appropriate serializer and de-serializer.
     * 
     * @param typeRegistry
     *            Type registry (Mapping from type name to class).
     * @param jsonbDeSer
     *            JSON-B serializer/deserializer to use.
     * 
     * @return New instance.
     */
    @NotNull
    public static SerDeserializerRegistry createSerDeserializerRegistry(final SerializedDataTypeRegistry typeRegistry,
                                                                        final JsonbDeSerializer jsonbDeSer) {

        final SimpleSerializerDeserializerRegistry registry = new SimpleSerializerDeserializerRegistry();

        // Base types always needed
        registry.add(IEscEvents.SER_TYPE, APPLICATION_JSON, jsonbDeSer);
        registry.add(IEscEvent.SER_TYPE, APPLICATION_JSON, jsonbDeSer);
        registry.add(IEscMeta.SER_TYPE, APPLICATION_JSON, jsonbDeSer);
        registry.add(IBase64Data.SER_TYPE, APPLICATION_JSON, jsonbDeSer);

        // User defined types
        for (final SerializedDataType type : USER_DEFINED_TYPES) {
            registry.add(type, APPLICATION_JSON, jsonbDeSer);
        }
        jsonbDeSer.init(typeRegistry, registry, registry);

        return registry;
    }

    /**
     * Creates a registry that connects the type with the appropriate serializer and de-serializer.
     * 
     * @return New instance.
     */
    public static SerDeserializerRegistry createRegistry() {

        // Knows about all types for usage with JSON-B
        final SerializedDataTypeRegistry typeRegistry = createTypeRegistry();

        // Does the actual marshalling/unmarshalling
        final JsonbDeSerializer jsonbDeSer = createJsonbDeSerializer();

        // Registry connects the type with the appropriate serializer and de-serializer
        return createSerDeserializerRegistry(typeRegistry, jsonbDeSer);

    }

    /**
     * Creates an instance of the JSON-B serializer/deserializer.
     * 
     * @return New instance that is fully initialized with al necessary settings.
     */
    public static JsonbDeSerializer createJsonbDeSerializer() {

        return JsonbDeSerializer.builder()
                .withSerializers(EscJsonbUtils.createEscJsonbSerializers())
                .withDeserializers(EscJsonbUtils.createEscJsonbDeserializers())
                .withAdapters(getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy())
                .withEncoding(StandardCharsets.UTF_8)
                .build();

    }

}