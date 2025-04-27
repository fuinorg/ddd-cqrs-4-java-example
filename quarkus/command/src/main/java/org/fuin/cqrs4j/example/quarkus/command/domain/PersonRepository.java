package org.fuin.cqrs4j.example.quarkus.command.domain;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.core.Repository;

/**
 * Event sourced repository for storing a {@link Person} aggregate.
 */
public interface PersonRepository extends Repository<PersonId, Person> {

}