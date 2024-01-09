package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.json.bind.annotation.JsonbCreator;

/**
 * DTO class sent back to the client. We could also serialize the entity instead.
 * In case you want to provide some Java library for the client, this class can be
 * moved to the API module without making JPA entity details public.
 *
 * @param type  Unique type name.
 * @param count Number of instances the type currently has.
 */
public record Statistic(String type, int count) {

    @JsonbCreator
    public Statistic {
    }

}
