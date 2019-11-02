package org.fuin.cqrs4j.example.javasecdi.shared.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.fuin.esc.spi.JsonbDeSerializer;
import org.fuin.esc.spi.SerDeserializerRegistry;
import org.fuin.esc.spi.SerializedDataTypeRegistry;

/**
 * CDI bean that creates a {@link SerDeserializerRegistry}.
 */
@ApplicationScoped
public class SharedSerDeserializerRegistryFactory {

    @Produces
    @ApplicationScoped
    public SerDeserializerRegistry createRegistry() {

        // Knows about all types for usage with JSON-B
        final SerializedDataTypeRegistry typeRegistry = SharedUtils.createTypeRegistry();

        // Does the actual marshalling/unmarshalling
        final JsonbDeSerializer jsonbDeSer = SharedUtils.createJsonbDeSerializer();

        // Registry connects the type with the appropriate serializer and de-serializer
        final SerDeserializerRegistry serDeserRegistry = SharedUtils.createSerDeserializerRegistry(typeRegistry,
                jsonbDeSer);

        return serDeserRegistry;

    }

}