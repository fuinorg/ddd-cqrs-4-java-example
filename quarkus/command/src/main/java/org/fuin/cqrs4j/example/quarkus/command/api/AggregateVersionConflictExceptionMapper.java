package org.fuin.cqrs4j.example.quarkus.command.api;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.fuin.cqrs4j.jsonb.SimpleResult;
import org.fuin.ddd4j.core.AggregateVersionConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps the exceptions into a HTTP status.
 */
@Provider
public class AggregateVersionConflictExceptionMapper implements ExceptionMapper<AggregateVersionConflictException> {

    private static final Logger LOG = LoggerFactory.getLogger(AggregateVersionConflictExceptionMapper.class);

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(final AggregateVersionConflictException ex) {

        LOG.info("{} {}", ex.getShortId(), ex.getMessage());

        return Response.status(Status.CONFLICT).entity(SimpleResult.error(ex.getShortId(), ex.getMessage())).type(headers.getMediaType())
                .build();
    }

}
