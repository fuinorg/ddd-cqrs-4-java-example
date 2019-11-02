package org.fuin.cqrs4j.example.javasecdi.qry.handler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.fuin.cqrs4j.EventDispatcher;
import org.fuin.cqrs4j.ProjectionService;
import org.fuin.esc.api.ProjectionStreamId;
import org.fuin.esc.api.StreamEventsSlice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class QryEventChunkHandler {

    private static final Logger LOG = LoggerFactory.getLogger(QryEventChunkHandler.class);
            
    /** Unique name of the event store projection that is used. */
    public static final ProjectionStreamId PROJECTION_STREAM_ID = new ProjectionStreamId("qry-person-stream");

    @Inject
    private EventDispatcher dispatcher;

    @Inject
    private ProjectionService projectionService;

    /**
     * Returns the next event position to read.
     * 
     * @return Number of the next event to read.
     */
    public Long readNextEventNumber() {
        return projectionService.readProjectionPosition(PROJECTION_STREAM_ID);
    }

    /**
     * Handles the current slice as a single transaction.
     * 
     * @param currentSlice
     *            Slice with events to dispatch.
     */
    public void handleChunk(final StreamEventsSlice currentSlice) {
        LOG.debug("Handle chunk: {}", currentSlice);
        dispatcher.dispatchCommonEvents(currentSlice.getEvents());
        projectionService.updateProjectionPosition(PROJECTION_STREAM_ID, currentSlice.getNextEventNumber());
    }

}