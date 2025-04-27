package org.fuin.cqrs4j.example.spring.command.domain;

import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.ddd4j.core.Repository;

/**
 * Event sourced repository for storing a {@link Person} aggregate.
 */
public interface PersonRepository extends Repository<PersonId, Person> {

}