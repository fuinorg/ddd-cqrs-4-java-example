package org.fuin.cqrs4j.example.quarkus.shared;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import org.fuin.ddd4j.core.EntityIdFactory;
import org.fuin.ddd4j.core.EventType;
import org.fuin.ddd4j.core.JandexEntityIdFactory;
import org.fuin.ddd4j.jsonb.AggregateVersionJsonbAdapter;
import org.fuin.ddd4j.jsonb.EntityIdJsonbAdapter;
import org.fuin.ddd4j.jsonb.EntityIdPathJsonbAdapter;
import org.fuin.ddd4j.jsonb.EventIdJsonbAdapter;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.api.SerializedDataType;
import org.fuin.esc.api.SerializedDataTypeRegistry;
import org.fuin.esc.api.SimpleSerializerDeserializerRegistry;
import org.fuin.esc.client.JandexSerializedDataTypeRegistry;
import org.fuin.esc.jsonb.Base64Data;
import org.fuin.esc.jsonb.EscEvent;
import org.fuin.esc.jsonb.EscEvents;
import org.fuin.esc.jsonb.EscJsonbUtils;
import org.fuin.esc.jsonb.EscMeta;
import org.fuin.esc.jsonb.JsonbDeSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.zip.Adler32;

/**
 * Utility code shared between command (write) and query (read) module.
 */
public final class SharedUtils {

    private static final String APPLICATION_JSON = "application/json";

    /** All types that will be written into and read from the event store. */
    private static final TypeClass[] USER_DEFINED_TYPES = new TypeClass[] {
            new TypeClass(PersonCreatedEvent.SER_TYPE, PersonCreatedEvent.class),
            new TypeClass(PersonDeletedEvent.SER_TYPE, PersonDeletedEvent.class)
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
                    new PersonId.Converter(),
                    new PersonName.Adapter()
            );
        }
        return JSONB_ADAPTERS.toArray(new JsonbAdapter<?, ?>[0]);
    }


    /**
     * Create a registry that allows finding types (classes) based on their unique type name.
     * 
     * @return New instance.
     */
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
    public static SerDeserializerRegistry createSerDeserializerRegistry(final SerializedDataTypeRegistry typeRegistry,
                                                                        final JsonbDeSerializer jsonbDeSer) {

        final SimpleSerializerDeserializerRegistry registry = new SimpleSerializerDeserializerRegistry();

        // Base types always needed
        registry.add(EscEvents.SER_TYPE, APPLICATION_JSON, jsonbDeSer);
        registry.add(EscEvent.SER_TYPE, APPLICATION_JSON, jsonbDeSer);
        registry.add(EscMeta.SER_TYPE, APPLICATION_JSON, jsonbDeSer);
        registry.add(Base64Data.SER_TYPE, APPLICATION_JSON, jsonbDeSer);

        // User defined types
        for (final TypeClass tc : USER_DEFINED_TYPES) {
            registry.add(tc.getType(), APPLICATION_JSON, jsonbDeSer);
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
        final SerializedDataTypeRegistry typeRegistry = SharedUtils.createTypeRegistry();

        // Does the actual marshalling/unmarshalling
        final JsonbDeSerializer jsonbDeSer = SharedUtils.createJsonbDeSerializer();

        // Registry connects the type with the appropriate serializer and de-serializer
        return SharedUtils.createSerDeserializerRegistry(typeRegistry, jsonbDeSer);

    }

    /**
     * Creates an instance of the JSON-B serializer/deserializer.
     * 
     * @return New instance that is fully initialized with al necessary settings.
     */
    public static JsonbDeSerializer createJsonbDeSerializer() {

        return JsonbDeSerializer.builder().withSerializers(EscJsonbUtils.createEscJsonbSerializers())
                .withDeserializers(EscJsonbUtils.createEscJsonbDeserializers()).withAdapters(getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy()).withEncoding(StandardCharsets.UTF_8).build();

    }

    /**
     * Creates an Adler32 checksum based on event type names.
     * 
     * @param eventTypes
     *            Types to calculate a checksum for.
     * 
     * @return Checksum based on all names.
     */
    public static long calculateChecksum(final Collection<EventType> eventTypes) {
        final Adler32 checksum = new Adler32();
        for (final EventType eventType : eventTypes) {
            checksum.update(eventType.asBaseType().getBytes(StandardCharsets.US_ASCII));
        }
        return checksum.getValue();
    }

    private static class FieldAccessStrategy implements PropertyVisibilityStrategy {

        @Override
        public boolean isVisible(Field field) {
            return true;
        }

        @Override
        public boolean isVisible(Method method) {
            return false;
        }

    }

    /**
     * Helper class for type/class combination.
     */
    private static final class TypeClass {

        private final SerializedDataType type;

        private final Class<?> clasz;

        /**
         * Constructor with all data.
         * 
         * @param type
         *            Type.
         * @param clasz
         *            Class.
         */
        public TypeClass(final SerializedDataType type, final Class<?> clasz) {
            super();
            this.type = type;
            this.clasz = clasz;
        }

        /**
         * Returns the type.
         * 
         * @return Type.
         */
        public SerializedDataType getType() {
            return type;
        }

        /**
         * Returns the class.
         * 
         * @return Class.
         */
        public Class<?> getClasz() {
            return clasz;
        }

    }

}