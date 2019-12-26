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
package org.fuin.cqrs4j.example.quarkus.query.handler;

import static org.fuin.cqrs4j.Cqrs4JUtils.tryLocked;
import static org.fuin.cqrs4j.example.quarkus.query.handler.QryEventChunkHandler.PROJECTION_STREAM_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.fuin.cqrs4j.EventDispatcher;
import org.fuin.cqrs4j.example.quarkus.query.app.QryCheckForViewUpdatesEvent;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.EventStore;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.api.TypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads incoming events from an attached event store and dispatches them to the appropriate event handlers.
 */
@ApplicationScoped
public class QryProjector {

    private static final Logger LOG = LoggerFactory.getLogger(QryProjector.class);

    /** Prevents more than one projector thread running at a time. */
    private static final Semaphore LOCK = new Semaphore(1);

    // The following beans are NOT thread safe!
    // Above LOCK prevents multithreaded access

    @Inject
    ProjectionAdminEventStore eventstore;

    @Inject
    EventStore eventStore;

    @Inject
    QryEventChunkHandler chunkHandler;

    @Inject
    EventDispatcher dispatcher;

    /**
     * Listens for timer events. If a second timer event occurs while the previous call is still being executed, the method will simply be
     * skipped.
     * 
     * @param event
     *            Timer event.
     */
    public void onEvent(@ObservesAsync final QryCheckForViewUpdatesEvent event) {
        tryLocked(LOCK, () -> {
            try {
                readStreamEvents();
            } catch (final RuntimeException ex) {
                LOG.error("Error reading events from stream", ex);
            }
        });
    }

    private void readStreamEvents() {

        // TODO Make sure a projection with the correct events exists
        // We must update the projection if new events are defined or some are removed!
        if (!eventstore.projectionExists(PROJECTION_STREAM_ID)) {
            final Set<EventType> eventTypes = dispatcher.getAllTypes();
            final List<TypeName> typeNames = new ArrayList<>();
            for (final EventType eventType : eventTypes) {
                typeNames.add(new TypeName(eventType.asBaseType()));
            }
            LOG.info("Create projection '{}' with events: {}", PROJECTION_STREAM_ID, typeNames);
            eventstore.createProjection(PROJECTION_STREAM_ID, true, typeNames);
        }

        // Read and dispatch events
        final Long nextEventNumber = chunkHandler.readNextEventNumber();
        eventStore.readAllEventsForward(PROJECTION_STREAM_ID, nextEventNumber, 100, (currentSlice) -> {
            chunkHandler.handleChunk(currentSlice);
        });

    }

}
