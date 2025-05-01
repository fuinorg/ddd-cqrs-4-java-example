package org.fuin.cqrs4j.example.spring.query.views.personlist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * DTO class sent back to the client. We could also serialize the entity instead.
 * In case you want to provide some Java library for the client, this class can be
 * moved to the API module without making JPA entity details public.
 *
 * @param id   Unique identifier of the person.
 * @param name Name of the person.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record Person(String id, String name) {
}
