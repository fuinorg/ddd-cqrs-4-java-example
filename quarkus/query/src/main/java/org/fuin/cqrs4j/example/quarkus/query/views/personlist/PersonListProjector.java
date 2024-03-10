package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import org.fuin.cqrs4j.example.quarkus.query.app.QryCheckForViewUpdatesEvent;
import org.fuin.ddd4j.core.EventType;
import org.fuin.esc.api.ProjectionAdminEventStore;
import org.fuin.esc.api.TypeName;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import static org.fuin.utils4j.Utils4J.tryLocked;

/**
 * Reads incoming events from an attached event store and dispatches them to the appropriate event handlers.
 */
@ApplicationScoped
public class PersonListProjector {

    private static final Logger LOG = LoggerFactory.getLogger(PersonListProjector.class);

    /** Prevents more than one projector thread running at a time. */
    private static final Semaphore LOCK = new Semaphore(1);

    // The following beans are NOT thread safe!
    // Above LOCK prevents multithreaded access

    @Inject
    IESGrpcEventStore eventstore;

    @Inject
    ProjectionAdminEventStore admin;

    @Inject
    PersonListEventChunkHandler chunkHandler;

    @Inject
    PersonListEventDispatcher dispatcher;

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

        // Create an event store projection if it does not exist.
        if (!admin.projectionExists(chunkHandler.getProjectionStreamId())) {
            final List<TypeName> typeNames = getEventTypeNames();
            LOG.info("Create projection '{}' with events: {}", chunkHandler.getProjectionStreamId(), typeNames);
            admin.createProjection(chunkHandler.getProjectionStreamId(), true, typeNames);
        }

        // Read and dispatch events
        final Long nextEventNumber = chunkHandler.readNextEventNumber();
        eventstore.readAllEventsForward(chunkHandler.getProjectionStreamId(), nextEventNumber, 100,
                currentSlice -> chunkHandler.handleChunk(currentSlice));

    }

    /**
     * Returns a list of all event type names used for this projection.
     * 
     * @return List of event names.
     */
    public List<TypeName> getEventTypeNames() {
        final List<TypeName> typeNames = new ArrayList<>();
        final Set<EventType> eventTypes = dispatcher.getAllTypes();
        for (final EventType eventType : eventTypes) {
            typeNames.add(new TypeName(eventType.asBaseType()));
        }
        return typeNames;
    }

}
