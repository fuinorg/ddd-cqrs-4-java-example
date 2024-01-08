package org.fuin.cqrs4j.example.aggregates;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.ddd4j.ddd.Repository;

/**
 * Event sourced repository for storing a {@link Person} aggregate.
 */
public interface PersonRepository extends Repository<PersonId, Person> {

}