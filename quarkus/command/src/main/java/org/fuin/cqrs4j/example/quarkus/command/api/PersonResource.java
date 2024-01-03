package org.fuin.cqrs4j.example.quarkus.command.api;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.fuin.cqrs4j.CommandExecutionFailedException;
import org.fuin.cqrs4j.SimpleResult;
import org.fuin.cqrs4j.example.aggregates.DuplicatePersonNameException;
import org.fuin.cqrs4j.example.aggregates.Person;
import org.fuin.cqrs4j.example.aggregates.PersonRepository;
import org.fuin.cqrs4j.example.shared.CreatePersonCommand;
import org.fuin.cqrs4j.example.shared.DeletePersonCommand;
import org.fuin.ddd4j.ddd.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import java.util.Optional;
import java.util.Set;

@Path("/persons")
public class PersonResource {

    @Inject
    PersonRepository repo;

    @Inject
    Validator validator;

    @Context
    UriInfo uriInfo;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response create(final CreatePersonCommand cmd)
            throws AggregateAlreadyExistsException, AggregateDeletedException, CommandExecutionFailedException {

        // Verify preconditions
        final Set<ConstraintViolation<CreatePersonCommand>> violations = validator.validate(cmd);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        try {
            // Create aggregate
            final Person person = new Person(cmd.getAggregateRootId(), cmd.getName(), name -> {
                // TODO Execute a call to the query side to verify if the name already exists
                return Optional.empty();
            });
            repo.add(person);

            // Send OK response
            return Response.ok(SimpleResult.ok()).build();
        } catch (final DuplicatePersonNameException ex) {
            throw new CommandExecutionFailedException(ex);
        }

    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    public Response delete(@PathParam("personId") final UUID personId, final DeletePersonCommand cmd)
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
        return Response.ok(SimpleResult.ok()).build();

    }

}
