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
package org.fuin.cqrs4j.example.spring.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.fuin.cqrs4j.CommandExecutionFailedException;
import org.fuin.cqrs4j.SimpleResult;
import org.fuin.ddd4j.ddd.AggregateAlreadyExistsException;
import org.fuin.ddd4j.ddd.AggregateDeletedException;
import org.fuin.ddd4j.ddd.AggregateNotFoundException;
import org.fuin.ddd4j.ddd.AggregateVersionConflictException;
import org.fuin.ddd4j.ddd.AggregateVersionNotFoundException;
import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.ExceptionShortIdentifable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Maps the exceptions into a HTTP status.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = AggregateAlreadyExistsException.class)
    public ResponseEntity<SimpleResult> exception(final AggregateAlreadyExistsException ex) {

        LOG.info("{} {}", ex.getShortId(), ex.getMessage());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(SimpleResult.error(ex.getShortId(), ex.getMessage()), headers, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(value = AggregateDeletedException.class)
    public ResponseEntity<SimpleResult> exception(final AggregateDeletedException ex) {

        LOG.info("{} {}", ex.getShortId(), ex.getMessage());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(SimpleResult.error(ex.getShortId(), ex.getMessage()), headers, HttpStatus.GONE);

    }

    @ExceptionHandler(value = AggregateNotFoundException.class)
    public ResponseEntity<SimpleResult> exception(final AggregateNotFoundException ex) {

        LOG.info("{} {}", ex.getShortId(), ex.getMessage());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(SimpleResult.error(ex.getShortId(), ex.getMessage()), headers, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = AggregateVersionConflictException.class)
    public ResponseEntity<SimpleResult> exception(final AggregateVersionConflictException ex) {

        LOG.info("{} {}", ex.getShortId(), ex.getMessage());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(SimpleResult.error(ex.getShortId(), ex.getMessage()), headers, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(value = AggregateVersionNotFoundException.class)
    public ResponseEntity<SimpleResult> exception(final AggregateVersionNotFoundException ex) {

        LOG.info("{} {}", ex.getShortId(), ex.getMessage());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(SimpleResult.error(ex.getShortId(), ex.getMessage()), headers, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = CommandExecutionFailedException.class)
    public ResponseEntity<SimpleResult> exception(final CommandExecutionFailedException ex) {

        final String shortId;
        if (ex.getCause() instanceof ExceptionShortIdentifable) {
            final ExceptionShortIdentifable esi = (ExceptionShortIdentifable) ex.getCause();
            shortId = esi.getShortId();
        } else {
            shortId = ex.getCause().getClass().getName();
        }

        LOG.info("{} {}", shortId, ex.getCause().getMessage());

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(SimpleResult.error(shortId, ex.getMessage()), headers, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<SimpleResult> exception(final ConstraintViolationException ex) {

        LOG.info("{} {}", CONSTRAINT_VIOLATION, "ConstraintViolationException");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(SimpleResult.error(CONSTRAINT_VIOLATION, asString(ex.getConstraintViolations())), headers, HttpStatus.BAD_REQUEST);

    }
    
    private static String asString(@Nullable final Set<ConstraintViolation<?>> constraintViolations) {
        if (constraintViolations == null || constraintViolations.size() == 0) {
            return "";
        }
        final List<String> list = new ArrayList<>();
        for (final ConstraintViolation<?> constraintViolation : constraintViolations) {
            list.add(Contract.asString(constraintViolation));
        }
        return list.toString();
    }
    
}
