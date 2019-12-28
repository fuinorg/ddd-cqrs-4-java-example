package org.fuin.cqrs4j.example.spring.query.handler;

import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.EventDispatcher;
import org.fuin.cqrs4j.SimpleEventDispatcher;
import org.fuin.cqrs4j.example.spring.query.domain.QryPerson;
import org.fuin.ddd4j.ddd.Event;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.CommonEvent;
import org.springframework.stereotype.Component;

/**
 * Dispatches events that relate to the {@link QryPerson} entity to the appropriate
 * event handers.
 */
@NotThreadSafe
@Component
public class PersonEventDispatcher implements EventDispatcher {

	private SimpleEventDispatcher delegate;

	/**
	 * Default constructor.
	 */
	public PersonEventDispatcher(final PersonCreatedEventHandler createdHandler) {
		super();
		delegate = new SimpleEventDispatcher(createdHandler);
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
