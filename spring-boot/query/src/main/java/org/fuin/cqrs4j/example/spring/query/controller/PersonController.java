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

import org.fuin.cqrs4j.example.spring.query.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PersonController {

	@Autowired
	private PersonRepository personRepository;

	/**
	 * Get all persons list.
	 *
	 * @return the list
	 */
	@GetMapping(path = "/persons", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Person> getAllQueryPersons() {
		return personRepository.findAll();
	}

	/**
	 * Reads a person by it's universally unique aggregate UUID.
	 *
	 * @param personId Person UUID.
	 * 
	 * @return Person from database.
	 * 
	 * @throws ResourceNotFoundException A person with the given UUID is unknown.
	 */
	@GetMapping(path = "/persons/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> getQueryPersonsById(@PathVariable(value = "id") String personId)
			throws ResourceNotFoundException {
		final Person person = personRepository.findById(personId)
				.orElseThrow(() -> new ResourceNotFoundException("A person with id '" + personId + "' was not found"));
		return ResponseEntity.ok().body(person);
	}

}
