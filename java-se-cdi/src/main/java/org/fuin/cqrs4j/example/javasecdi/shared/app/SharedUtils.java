package org.fuin.cqrs4j.example.javasecdi.shared.app;

import jakarta.json.bind.adapter.JsonbAdapter;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.ddd4j.ddd.EntityIdPathConverter;
import org.fuin.ddd4j.ddd.EventIdConverter;
import org.fuin.esc.spi.*;

import java.nio.charset.Charset;

/**
 * Utility code shared between command (write) and query (read) module.
 */
public final class SharedUtils {

    /** All types that will be written into and read from the event store. */
    private static TypeClass[] USER_DEFINED_TYPES = new TypeClass[] {
            new TypeClass(PersonCreatedEvent.SER_TYPE, PersonCreatedEvent.class) };

    /** All JSON-B adapters from this module. */
    public static JsonbAdapter<?, ?>[] JSONB_ADAPTERS = new JsonbAdapter<?, ?>[] { new EventIdConverter(),
            new EntityIdPathConverter(new SharedEntityIdFactory()), new PersonId.Converter(), new PersonName.Converter() };

    private SharedUtils() {
        throw new UnsupportedOperationException("It is not allowed to create an instance of a utiliy class");
    }

    /**
     * Create a registry that allows finding types (classes) based on their unique type name.
     * 
     * @return New instance.
     */
    public static SerializedDataTypeRegistry createTypeRegistry() {

        // Contains all types for usage with JSON-B
        final SimpleSerializedDataTypeRegistry typeRegistry = new SimpleSerializedDataTypeRegistry();

        // Base types always needed
        typeRegistry.add(EscEvent.SER_TYPE, EscEvent.class);
        typeRegistry.add(EscEvents.SER_TYPE, EscEvents.class);
        typeRegistry.add(EscMeta.SER_TYPE, EscMeta.class);
        typeRegistry.add(Base64Data.SER_TYPE, Base64Data.class);

        // User defined types
        for (final TypeClass tc : USER_DEFINED_TYPES) {
            typeRegistry.add(tc.getType(), tc.getClasz());
        }
        return typeRegistry;

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
        registry.add(EscEvents.SER_TYPE, "application/json", jsonbDeSer);
        registry.add(EscEvent.SER_TYPE, "application/json", jsonbDeSer);
        registry.add(EscMeta.SER_TYPE, "application/json", jsonbDeSer);
        registry.add(Base64Data.SER_TYPE, "application/json", jsonbDeSer);

        // User defined types
        for (final TypeClass tc : USER_DEFINED_TYPES) {
            registry.add(tc.getType(), "application/json", jsonbDeSer);
        }
        jsonbDeSer.init(typeRegistry, registry, registry);

        return registry;
    }

    /**
     * Creates an instance of the JSON-B serializer/deserializer.
     * 
     * @return New instance that is fully initialized with al necessary settings.
     */
    public static JsonbDeSerializer createJsonbDeSerializer() {

        return JsonbDeSerializer.builder().withSerializers(EscSpiUtils.createEscJsonbSerializers())
                .withDeserializers(EscSpiUtils.createEscJsonbDeserializers()).withAdapters(JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy()).withEncoding(Charset.forName("utf-8")).build();

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