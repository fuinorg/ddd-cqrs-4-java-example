package org.fuin.cqrs4j.example.spring.query.views.personlist;

import jakarta.validation.constraints.NotNull;
import org.fuin.cqrs4j.esc.EventDispatcher;
import org.fuin.cqrs4j.esc.SimpleEventDispatcher;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.fuin.esc.api.CommonEvent;
import org.fuin.objects4j.common.NotThreadSafe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Dispatches events that relate to the {@link PersonListEntry} entity to the appropriate event handers.
 */
@NotThreadSafe
@Component
public class PersonListEventDispatcher implements EventDispatcher {

    private final SimpleEventDispatcher delegate;

    /**
     * Default constructor.
     */
    public PersonListEventDispatcher(final PersonCreatedEventHandler createdHandler,
                                     final PersonDeletedEventHandler deletedHandler) {
        super();
        delegate = new SimpleEventDispatcher(createdHandler, deletedHandler);
    }

    @Override
    public @NotNull Set<EventType> getAllTypes() {
        return delegate.getAllTypes();
    }

    @Override
    public void dispatchCommonEvents(@NotNull List<CommonEvent> commonEvents) {
        delegate.dispatchCommonEvents(commonEvents);
    }

    @Override
    public void dispatchEvents(@NotNull List<Event> events) {
        delegate.dispatchEvents(events);
    }

    @Override
    public void dispatchEvent(@NotNull Event event) {
        delegate.dispatchEvent(event);
    }

}
