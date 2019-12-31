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
package org.fuin.cqrs4j.example.spring.command.controller;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.fuin.cqrs4j.CommandExecutionFailedException;
import org.fuin.cqrs4j.SimpleResult;
import org.fuin.cqrs4j.example.aggregates.DuplicatePersonNameException;
import org.fuin.cqrs4j.example.aggregates.Person;
import org.fuin.cqrs4j.example.aggregates.PersonRepository;
import org.fuin.cqrs4j.example.shared.CreatePersonCommand;
import org.fuin.ddd4j.ddd.AggregateAlreadyExistsException;
import org.fuin.ddd4j.ddd.AggregateDeletedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class PersonController {

	@Autowired
	private PersonRepository repo;

	@Autowired
	private Validator validator;

	@PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SimpleResult> create(@RequestBody final CreatePersonCommand cmd)
			throws AggregateAlreadyExistsException, AggregateDeletedException, CommandExecutionFailedException,
			DuplicatePersonNameException {

		// Verify preconditions
		final Set<ConstraintViolation<CreatePersonCommand>> violations = validator.validate(cmd);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		// Create aggregate
		final Person person = new Person(cmd.getAggregateRootId(), cmd.getName(), (name) -> {
			// TODO Execute a call to the query side to verify if the name already exists
			return Optional.empty();
		});
		repo.add(person);

		// Send OK response
		return new ResponseEntity<>(SimpleResult.ok(), HttpStatus.OK);

	}

}
