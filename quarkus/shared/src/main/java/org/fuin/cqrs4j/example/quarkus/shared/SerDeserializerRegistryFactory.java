package org.fuin.cqrs4j.example.quarkus.shared;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.esc.api.SerDeserializerRegistry;

/**
 * CDI bean that creates a {@link SerDeserializerRegistry}.
 */
@ApplicationScoped
public class SerDeserializerRegistryFactory {

    @Produces
    @ApplicationScoped
    public SerDeserializerRegistry createRegistry() {
        return SharedUtils.createRegistry();
    }

}