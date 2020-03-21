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
package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.fuin.cqrs4j.ProjectionService;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.ProjectionStreamId;
import org.fuin.esc.api.StreamEventsSlice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class PersonListEventChunkHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PersonListEventChunkHandler.class);

    @Inject
    PersonListEventDispatcher dispatcher;

    @Inject
    ProjectionService projectionService;

    private ProjectionStreamId streamId;

    /**
     * Returns the name of the event store projection that is used by this handler.
     * 
     * @return Unique projection stream name.
     */
    public ProjectionStreamId getProjectionStreamId() {
        if (streamId == null) {
            final Set<EventType> eventTypes = dispatcher.getAllTypes();
            final String name = "quarkus-qry-person-" + SharedUtils.calculateChecksum(eventTypes);
            streamId = new ProjectionStreamId(name);
        }
        return streamId;
    }

    /**
     * Returns the next event position to read.
     * 
     * @return Number of the next event to read.
     */
    public Long readNextEventNumber() {
        return projectionService.readProjectionPosition(getProjectionStreamId());
    }

    /**
     * Handles the current slice as a single transaction.
     * 
     * @param currentSlice
     *            Slice with events to dispatch.
     */
    @Transactional
    public void handleChunk(final StreamEventsSlice currentSlice) {
        LOG.debug("Handle chunk: {}", currentSlice);
        dispatcher.dispatchCommonEvents(currentSlice.getEvents());
        projectionService.updateProjectionPosition(getProjectionStreamId(), currentSlice.getNextEventNumber());
    }

}