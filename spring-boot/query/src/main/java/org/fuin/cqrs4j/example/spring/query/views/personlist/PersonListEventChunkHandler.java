package org.fuin.cqrs4j.example.spring.query.views.personlist;

import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.fuin.cqrs4j.ProjectionService;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.ProjectionStreamId;
import org.fuin.esc.api.StreamEventsSlice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dispatches the events to the event handlers that will update the database and
 * stores the next event position in the database (All in the same transaction).
 */
@NotThreadSafe
@Component
public class PersonListEventChunkHandler {

	private static final Logger LOG = LoggerFactory.getLogger(PersonListEventChunkHandler.class);

	/** Unique name of the event store projection that is used. */
	public static final ProjectionStreamId PROJECTION_STREAM_ID = new ProjectionStreamId("spring-qry-person-stream");

	@Autowired
	private PersonListEventDispatcher dispatcher;

	@Autowired
	private ProjectionService projectionService;

    private ProjectionStreamId streamId;

    /**
     * Returns the name of the event store projection that is used by this handler.
     * 
     * @return Unique projection stream name.
     */
    public ProjectionStreamId getProjectionStreamId() {
        if (streamId == null) {
            final Set<EventType> eventTypes = dispatcher.getAllTypes();
            final String name = "spring-qry-person-" + SharedUtils.calculateChecksum(eventTypes);
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
		return projectionService.readProjectionPosition(PROJECTION_STREAM_ID);
	}

	/**
	 * Handles the current slice as a single transaction.
	 * 
	 * @param currentSlice Slice with events to dispatch.
	 */
	@Transactional
	public void handleChunk(final StreamEventsSlice currentSlice) {
		LOG.debug("Handle chunk: {}", currentSlice);
		dispatcher.dispatchCommonEvents(currentSlice.getEvents());
		projectionService.updateProjectionPosition(PROJECTION_STREAM_ID, currentSlice.getNextEventNumber());
	}

}