package org.fuin.cqrs4j.example.javasecdi.qry.domain;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

/**
 * Database service for persons.
 */
@Repository(forEntity = QryPerson.class)
public interface QryPersonRepository extends EntityRepository<QryPerson, String> {

}