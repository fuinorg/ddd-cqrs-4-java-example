package org.fuin.cqrs4j.example.spring.command.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.fuin.cqrs4j.core.CommandExecutionFailedException;
import org.fuin.cqrs4j.jsonb.SimpleResult;
import org.fuin.cqrs4j.example.aggregates.DuplicatePersonNameException;
import org.fuin.cqrs4j.example.aggregates.Person;
import org.fuin.cqrs4j.example.aggregates.PersonRepository;
import org.fuin.cqrs4j.example.shared.CreatePersonCommand;
import org.fuin.cqrs4j.example.shared.DeletePersonCommand;
import org.fuin.ddd4j.core.AggregateAlreadyExistsException;
import org.fuin.ddd4j.core.AggregateDeletedException;
import org.fuin.ddd4j.core.AggregateNotFoundException;
import org.fuin.ddd4j.core.AggregateVersionConflictException;
import org.fuin.ddd4j.core.AggregateVersionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/persons")
public final class PersonController {

    private final PersonRepository repo;

    private final Validator validator;

    public PersonController(PersonRepository repo, Validator validator) {
        this.repo = repo;
        this.validator = validator;
    }

    @PostMapping(path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleResult> create(@RequestBody final CreatePersonCommand cmd) throws AggregateAlreadyExistsException,
            AggregateDeletedException, CommandExecutionFailedException, DuplicatePersonNameException {

        // Verify preconditions
        final Set<ConstraintViolation<CreatePersonCommand>> violations = validator.validate(cmd);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Create aggregate
        final Person person = new Person(cmd.getAggregateRootId(), cmd.getName(), name -> {
            // TODO Execute a call to the query side to verify if the name already exists
            return Optional.empty();
        });
        repo.add(person);

        // Send OK response
        return new ResponseEntity<>(SimpleResult.ok(), HttpStatus.OK);

    }

    @DeleteMapping(path = "/{personId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleResult> delete(@PathVariable("personId") final UUID personId, @RequestBody final DeletePersonCommand cmd)
            throws AggregateVersionConflictException, AggregateVersionNotFoundException,
            AggregateDeletedException, AggregateNotFoundException {

        // Verify preconditions
        final Set<ConstraintViolation<DeletePersonCommand>> violations = validator.validate(cmd);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Read last known entity version
        final Person person = repo.read(cmd.getAggregateRootId(), cmd.getAggregateVersionInteger());

        // Try to delete the aggregate
        // Internally just sets a 'deleted' flag
        person.delete();

        // Write resulting events back to the repository
        // DO NOT call "repo.delete(..)! If you would do, you would never see a "deleted" event...
        // The repository "delete" really removes the stream and is more like a "purge".
        repo.update(person);

        // Send OK response
        return new ResponseEntity<>(SimpleResult.ok(), HttpStatus.OK);

    }

}
