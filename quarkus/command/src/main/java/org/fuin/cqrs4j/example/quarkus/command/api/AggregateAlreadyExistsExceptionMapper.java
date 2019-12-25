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

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.fuin.cqrs4j.SimpleResult;
import org.fuin.ddd4j.ddd.AggregateAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps the exceptions into a HTTP status.
 */
@Provider
public class AggregateAlreadyExistsExceptionMapper implements ExceptionMapper<AggregateAlreadyExistsException> {

    private static final Logger LOG = LoggerFactory.getLogger(AggregateAlreadyExistsExceptionMapper.class);

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(final AggregateAlreadyExistsException ex) {

        LOG.info("{} {}", ex.getShortId(), ex.getMessage());

        return Response.status(Status.CONFLICT).entity(SimpleResult.error(ex.getShortId(), ex.getMessage()))
                .type(headers.getMediaType()).build();
    }

}
