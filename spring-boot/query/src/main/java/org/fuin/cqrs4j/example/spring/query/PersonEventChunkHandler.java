package org.fuin.cqrs4j.example.spring.query;

import javax.annotation.concurrent.NotThreadSafe;

import org.fuin.cqrs4j.ProjectionService;
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
public class PersonEventChunkHandler {

	private static final Logger LOG = LoggerFactory.getLogger(PersonEventChunkHandler.class);

	/** Unique name of the event store projection that is used. */
	public static final ProjectionStreamId PROJECTION_STREAM_ID = new ProjectionStreamId("qry-person-stream");

	@Autowired
	private PersonEventDispatcher dispatcher;

	@Autowired
	private ProjectionService projectionService;

	/**
	 * Returns the next event position to read.
	 * 
	 * @return Number of the next event to read.
	 */
	@Transactional(readOnly = true)
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