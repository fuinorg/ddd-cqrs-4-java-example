package org.fuin.cqrs4j.example.quarkus.shared;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.shared.SharedUtils;

/**
 * CDI factory that creates a JSON-B instance.
 */
@ApplicationScoped
public class JsonbFactory {

    /**
     * Creates a JSON-B instance.
     * 
     * @return Fully configured instance.
     */
    @Produces
    public Jsonb createJsonb() {
        final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.getJsonbAdapters())
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        return JsonbBuilder.create(config);
    }

}
