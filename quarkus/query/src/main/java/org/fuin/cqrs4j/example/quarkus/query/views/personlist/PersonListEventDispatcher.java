package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.EventDispatcher;
import org.fuin.cqrs4j.SimpleEventDispatcher;
import org.fuin.ddd4j.ddd.Event;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.CommonEvent;

import java.util.List;
import java.util.Set;

/**
 * Dispatches events for the person list view.
 */
@ApplicationScoped
public class PersonListEventDispatcher implements EventDispatcher {

    private final SimpleEventDispatcher delegate;

    /**
     * Constructor with all events to be dispatched.
     * 
     * @param createdHandler
     *            PersonCreatedEventHandler.
     * @param deletedHandler
     *            PersonDeletedEventHandler.
     */
    public PersonListEventDispatcher(final PersonCreatedEventHandler createdHandler,
                                     final PersonDeletedEventHandler deletedHandler) {
        super();
        this.delegate = new SimpleEventDispatcher(createdHandler, deletedHandler);
    }

    @Override
    @NotNull
    public Set<EventType> getAllTypes() {
        return delegate.getAllTypes();
    }

    @Override
    public void dispatchCommonEvents(@NotNull final List<CommonEvent> commonEvents) {
        delegate.dispatchCommonEvents(commonEvents);
    }

    @Override
    public void dispatchEvents(@NotNull final List<Event> events) {
        delegate.dispatchEvents(events);
    }

    @Override
    public void dispatchEvent(@NotNull final Event event) {
        delegate.dispatchEvent(event);
    }

}
