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
package org.fuin.cqrs4j.example.quarkus.command.api;

import java.util.Optional;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
import org.fuin.ddd4j.ddd.AggregateAlreadyExistsException;
import org.fuin.ddd4j.ddd.AggregateDeletedException;

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

}
