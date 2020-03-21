package org.fuin.cqrs4j.example.spring.query.views.personlist;

import static org.fuin.cqrs4j.Cqrs4JUtils.tryLocked;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;
import javax.annotation.concurrent.ThreadSafe;

import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.TypeName;
import org.fuin.esc.eshttp.IESHttpEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Reads incoming events from an event store projection and dispatches them to
 * the appropriate event handlers. The event store projection will be created by
 * this class, if it does not yet exist.
 */
@ThreadSafe
@Component
public class PersonListProjector {

	private static final Logger LOG = LoggerFactory.getLogger(PersonListProjector.class);

	/** Prevents more than one projector thread running at a time. */
	private static final Semaphore LOCK = new Semaphore(1);

	private static AtomicBoolean APP_STARTED = new AtomicBoolean(false); 
	
	// The following beans are NOT thread safe!
	// Above LOCK prevents multithreaded access

	@Autowired
	private IESHttpEventStore eventstore;

	@Autowired
	private PersonListEventChunkHandler chunkHandler;

	@Autowired
	private PersonListEventDispatcher dispatcher;

	/**
	 * Runs triggered by the timer. If a second timer event occurs while the
	 * previous call is still being executed, the method execution will simply be
	 * skipped.
	 */
	@Scheduled(fixedRate = 100)
	@Async("projectorExecutor")
	public void execute() {
		if (!APP_STARTED.get()) {
			// Do nothing until application started
			return;
		}
		tryLocked(LOCK, () -> {
			try {
				readStreamEvents();
			} catch (final RuntimeException ex) {
				LOG.error("Error reading events from stream", ex);
			}
		});
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void onAppStart() {
		LOG.info("Application started");
		APP_STARTED.set(true);
	}

	@PreDestroy
    public void destroy() {
		APP_STARTED.set(false);
		LOG.info("Application stopped");
    }
	
    private void readStreamEvents() {

        // Create a projection if it does not exist.
        // Multiple projections (even in different microservice versions)
        // might share the same projection based on the event name hash.
        if (!eventstore.projectionExists(chunkHandler.getProjectionStreamId())) {
            final List<TypeName> typeNames = getEventTypeNames();
            LOG.info("Create projection '{}' with events: {}", chunkHandler.getProjectionStreamId(), typeNames);
            eventstore.createProjection(chunkHandler.getProjectionStreamId(), true, typeNames);
        }

        // Read and dispatch events
        final Long nextEventNumber = chunkHandler.readNextEventNumber();
        eventstore.readAllEventsForward(chunkHandler.getProjectionStreamId(), nextEventNumber, 100, (currentSlice) -> {
            chunkHandler.handleChunk(currentSlice);
        });

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
