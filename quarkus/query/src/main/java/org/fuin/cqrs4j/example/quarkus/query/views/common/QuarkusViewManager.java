package org.fuin.cqrs4j.example.quarkus.query.views.common;

import io.quarkus.arc.All;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.runtime.Shutdown;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.fuin.cqrs4j.ProjectionService;
import org.fuin.cqrs4j.example.shared.SharedUtils;
import org.fuin.cqrs4j.example.shared.View;
import org.fuin.ddd4j.ddd.Event;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.*;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import static org.fuin.cqrs4j.Cqrs4JUtils.tryLocked;

/**
 * Creates scheduler update jobs for all classes implementing the {@link View} interface.
 * Avoids boilerplate code: Instead of having a separated "Projector", "EventDispatcher"
 * and a "ChunkHandler" class for each view, there is only one simplified "View" class now.
 */
@ApplicationScoped
public class QuarkusViewManager {

    private static final Logger LOG = LoggerFactory.getLogger(QuarkusViewManager.class);

    @Inject
    Scheduler scheduler;

    @Inject
    @All
    List<View> rawViews;

    @Inject
    IESGrpcEventStore eventstore;

    @Inject
    ProjectionAdminEventStore admin;

    @Inject
    ProjectionService projectionService;

    private List<ViewExt> views;

    @Startup
    void createViews() {
        LOG.info("Create views...");
        views = rawViews.stream().map(ViewExt::new).toList();
        for (final ViewExt view : views) {
            LOG.info("Create: {}", view.getName());
            scheduler.newJob(view.getName())
                    .setCron(view.getCron())
                    .setTask(executionContext -> updateView(view))
                    .schedule();
        }
    }

    @Shutdown
    void shutdownViews() {
        LOG.info("Shutdown views...");
        for (final ViewExt view : views) {
            LOG.info("Shutdown: {}", view.getName());
            scheduler.unscheduleJob(view.getName());
        }
    }

    private void updateView(final ViewExt view) {
        tryLocked(view.getLock(), () -> {
            new Thread(() -> {
                try {
                    readStreamEvents(view);
                } catch (final RuntimeException ex) {
                    LOG.error("Error reading events from stream", ex);
                }
            }
            ).start();
        });
    }

    private void readStreamEvents(final ViewExt view) {

        // Create an event store projection if it does not exist.
        if (!admin.projectionExists(view.getProjectionStreamId())) {
            final List<TypeName> typeNames = asTypeNames(view.getEventTypes());
            LOG.info("Create projection '{}' with events: {}", view.getProjectionStreamId(), typeNames);
            admin.createProjection(view.getProjectionStreamId(), true, typeNames);
        }

        // Read and dispatch events
        final Long nextEventNumber = projectionService.readProjectionPosition(view.getProjectionStreamId());
        eventstore.readAllEventsForward(view.getProjectionStreamId(), nextEventNumber, view.getChunkSize(),
                currentSlice -> handleChunk(view, currentSlice));

    }

    private List<TypeName> asTypeNames(Set<EventType> eventTypes) {
        return eventTypes.stream().map(eventType -> new TypeName((eventType.asString()))).toList();
    }

    private void handleChunk(final ViewExt view, final StreamEventsSlice currentSlice) {
        QuarkusTransaction.requiringNew()
                .timeout(10)
                .call(() -> {
                    LOG.debug("Handle chunk: {}", currentSlice);
                    view.handleEvents(asEvents(currentSlice.getEvents()));
                    projectionService.updateProjectionPosition(view.getProjectionStreamId(), currentSlice.getNextEventNumber());
                    return 0;
                });
    }

    private List<org.fuin.ddd4j.ddd.Event> asEvents(List<CommonEvent> events) {
        return events.stream().map(event -> (Event) event.getData()).toList();
    }

    /**
     * Extends the view with some necessary values used only by this class.
     */
    private static class ViewExt implements View {

        private final View delegate;

        private final ProjectionStreamId projectionStreamId;

        private final Semaphore lock;

        public ViewExt(final View delegate) {
            this.delegate = delegate;

            final Set<EventType> eventTypes = delegate.getEventTypes();
            final String name = delegate.getName() + "-" + SharedUtils.calculateChecksum(eventTypes);
            projectionStreamId = new ProjectionStreamId(name);

            this.lock = new Semaphore(1);

        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public String getCron() {
            return delegate.getCron();
        }

        @Override
        public Set<EventType> getEventTypes() {
            return delegate.getEventTypes();
        }

        @Override
        public int getChunkSize() {
            return delegate.getChunkSize();
        }

        @Override
        public void handleEvents(List<Event> events) {
            delegate.handleEvents(events);
        }

        public ProjectionStreamId getProjectionStreamId() {
            return projectionStreamId;
        }

        public Semaphore getLock() {
            return lock;
        }

    }

}
