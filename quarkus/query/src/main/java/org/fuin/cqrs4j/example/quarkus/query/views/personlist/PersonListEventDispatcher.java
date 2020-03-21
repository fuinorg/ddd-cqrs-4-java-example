/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see
 * http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;

import org.fuin.cqrs4j.EventDispatcher;
import org.fuin.cqrs4j.SimpleEventDispatcher;
import org.fuin.ddd4j.ddd.Event;
import org.fuin.ddd4j.ddd.EventType;
import org.fuin.esc.api.CommonEvent;

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
     */
    public PersonListEventDispatcher(final PersonCreatedEventHandler createdHandler) {
        super();
        this.delegate = new SimpleEventDispatcher(createdHandler);
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
