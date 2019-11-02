package org.fuin.cqrs4j.example.javasecdi.qry.handler;

import static org.fuin.cqrs4j.Cqrs4JUtils.tryLocked;
import static org.fuin.cqrs4j.example.javasecdi.qry.handler.QryEventChunkHandler.PROJECTION_STREAM_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.fuin.cqrs4j.EventDispatcher;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.cqrs4j.example.javasecdi.qry.app.QryCheckForViewUpdatesEvent;
import org.fuin.esc.api.EventStore;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.api.TypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads incoming events from an attached event store and dispatches them to the appropriate event handlers.
 */
public class QryProjector {

    private static final Logger LOG = LoggerFactory.getLogger(QryProjector.class);

    /** Prevents more than one projector thread running at a time. */
    private static final Semaphore LOCK = new Semaphore(1);

    // The following beans are NOT thread safe!
    // Above LOCK prevents multithreaded access

    @Inject
    private ProjectionAdminEventStore eventstore;

    @Inject
    private EventStore eventStore;

    @Inject
    private QryEventChunkHandler chunkHandler;

    @Inject
    private EventDispatcher dispatcher;

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
