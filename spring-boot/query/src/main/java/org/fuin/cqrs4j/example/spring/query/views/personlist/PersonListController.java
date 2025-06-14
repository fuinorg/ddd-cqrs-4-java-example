package org.fuin.cqrs4j.example.spring.query.views.personlist;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.ddd4j.core.AggregateNotFoundException;
import org.fuin.objects4j.core.UUIDStr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller providing the persons.
 */
@RestController
@RequestMapping("/persons")
@Transactional(readOnly = true)
public class PersonListController {

    private static final Logger LOG = LoggerFactory.getLogger(PersonListController.class);

    @Autowired
    private EntityManager em;

    /**
     * Get all persons list.
     *
     * @return the list
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> getAllPersons() {
        final List<PersonListEntry> persons = em.createNamedQuery(PersonListEntry.FIND_ALL, PersonListEntry.class).getResultList();
        LOG.info("getAllPersons() = {}", persons.size());
        return persons.stream().map(PersonListEntry::toDto).toList();
    }

    /**
     * Reads a person by it's universally unique aggregate UUID.
     *
     * @param personId
     *            Person UUID.
     * 
     * @return Person from database.
     * 
     * @throws AggregateNotFoundException
     *             A person with the given identifier is unknown.
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getPersonById(@PathVariable(value = "id") @UUIDStr String personId)
            throws AggregateNotFoundException {

        final PersonListEntry person = em.find(PersonListEntry.class, personId);
        if (person == null) {
            throw new AggregateNotFoundException(PersonId.TYPE, new PersonId(UUID.fromString(personId)));
        }
        LOG.info("getPersonById({}) = {}", personId, person);
        return ResponseEntity.ok().body(person.toDto());
    }

}
