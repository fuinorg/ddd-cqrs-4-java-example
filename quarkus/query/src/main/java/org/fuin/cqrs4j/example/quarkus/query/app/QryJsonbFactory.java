package org.fuin.cqrs4j.example.quarkus.query.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.quarkus.shared.SharedUtils;

/**
 * CDI factory that creates a JSON-B instance.
 */
@ApplicationScoped
public class QryJsonbFactory {

    /**
     * Creates a JSON-B instance.
     * 
     * @return Fully configured instance.
     */
    @Produces
    public Jsonb createJsonb() {
        final JsonbConfig config = new JsonbConfig()
                .withAdapters(SharedUtils.JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        final Jsonb jsonb = JsonbBuilder.create(config);
        return jsonb;
    }

}
