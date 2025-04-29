package org.fuin.cqrs4j.example.spring.query.views.statistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * DTO class sent back to the client. We could also serialize the entity instead.
 * In case you want to provide some Java library for the client, this class can be
 * moved to the API module without making JPA entity details public.
 *
 * @param type  Unique type name.
 * @param count Number of instances the type currently has.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Statistic(String type, int count) {

}
