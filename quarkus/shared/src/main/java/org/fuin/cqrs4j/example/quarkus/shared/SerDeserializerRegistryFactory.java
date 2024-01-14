package org.fuin.cqrs4j.example.quarkus.shared;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.esc.api.JsonbDeSerializer;
import org.fuin.esc.api.SerDeserializerRegistry;
import org.fuin.esc.api.SerializedDataTypeRegistry;

/**
 * CDI bean that creates a {@link SerDeserializerRegistry}.
 */
@ApplicationScoped
public class SerDeserializerRegistryFactory {

    @Produces
    @ApplicationScoped
    public SerDeserializerRegistry createRegistry() {

        // Knows about all types for usage with JSON-B
        final SerializedDataTypeRegistry typeRegistry = SharedUtils.createTypeRegistry();

        // Does the actual marshalling/unmarshalling
        final JsonbDeSerializer jsonbDeSer = SharedUtils.createJsonbDeSerializer();

        // Registry connects the type with the appropriate serializer and de-serializer
        return SharedUtils.createSerDeserializerRegistry(typeRegistry, jsonbDeSer);

    }

}