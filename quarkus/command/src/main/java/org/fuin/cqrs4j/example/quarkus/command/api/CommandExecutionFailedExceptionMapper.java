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

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.fuin.cqrs4j.CommandExecutionFailedException;
import org.fuin.cqrs4j.SimpleResult;
import org.fuin.objects4j.common.ExceptionShortIdentifable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps the exceptions into a HTTP status.
 */
@Provider
public class CommandExecutionFailedExceptionMapper implements ExceptionMapper<CommandExecutionFailedException> {

    private static final Logger LOG = LoggerFactory.getLogger(CommandExecutionFailedExceptionMapper.class);

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(final CommandExecutionFailedException ex) {

        final String shortId;
        if (ex.getCause() instanceof ExceptionShortIdentifable) {
            final ExceptionShortIdentifable esi = (ExceptionShortIdentifable) ex.getCause();
            shortId = esi.getShortId();
        } else {
            shortId = ex.getCause().getClass().getName();
        }

        LOG.info("{} {}", shortId, ex.getCause().getMessage());

        return Response.status(Status.BAD_REQUEST).entity(SimpleResult.error(shortId, ex.getMessage())).type(headers.getMediaType())
                .build();
    }

}
