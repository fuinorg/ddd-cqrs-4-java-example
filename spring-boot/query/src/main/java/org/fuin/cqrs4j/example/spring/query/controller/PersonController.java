/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see
 * http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.spring.query.controller;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.spring.query.views.personlist.PersonListEntry;
import org.fuin.ddd4j.ddd.AggregateNotFoundException;
import org.fuin.objects4j.vo.UUIDStr;
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

@RestController
@RequestMapping("/persons")
@Transactional(readOnly = true)
public class PersonController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private EntityManager em;

	/**
	 * Get all persons list.
	 *
	 * @return the list
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonListEntry> getAllPersons() {
        final List<PersonListEntry> persons = em.createNamedQuery(PersonListEntry.FIND_ALL, PersonListEntry.class).getResultList();
        LOG.info("getAllPersons() = {}", persons.size());
		return persons;
	}

	/**
	 * Reads a person by it's universally unique aggregate UUID.
	 *
	 * @param personId Person UUID.
	 * 
	 * @return Person from database.
	 * 
	 * @throws AggregateNotFoundException A person with the given identifier is
	 *                                 unknown.
	 */
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonListEntry> getPersonsById(@PathVariable(value = "id") @UUIDStr String personId)
			throws AggregateNotFoundException {
		
        final PersonListEntry person = em.find(PersonListEntry.class, personId);
        if (person == null) {
        	throw new AggregateNotFoundException(PersonId.TYPE, new PersonId(UUID.fromString(personId)));
        }
        LOG.info("getPersonsById({}) = {}", personId, person);
		return ResponseEntity.ok().body(person);
	}

}
