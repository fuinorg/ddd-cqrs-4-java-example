package org.fuin.cqrs4j.example.quarkus.command.api;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.fuin.cqrs4j.SimpleResult;
import org.fuin.objects4j.common.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Maps the exceptions into a HTTP status.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";

    private static final Logger LOG = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(final ConstraintViolationException ex) {

        LOG.info("{} {}", CONSTRAINT_VIOLATION, "ConstraintViolationException");

        return Response.status(Status.BAD_REQUEST).entity(SimpleResult.error(CONSTRAINT_VIOLATION, asString(ex.getConstraintViolations())))
                .build();

    }

    private static String asString(@Nullable final Set<ConstraintViolation<?>> constraintViolations) {
        if (constraintViolations == null || constraintViolations.isEmpty()) {
            return "";
        }
        final List<String> list = new ArrayList<>();
        for (final ConstraintViolation<?> constraintViolation : constraintViolations) {
            list.add(Contract.asString(constraintViolation));
        }
        return list.toString();
    }

}
