package org.fuin.cqrs4j.example.spring.query.views.personlist;


import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.JpaView;
import org.fuin.cqrs4j.esc.JpaEventDispatcher;
import org.fuin.cqrs4j.esc.SimpleJpaEventDispatcher;
import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * View with persons.
 */
public class PersonListView implements JpaView {

    private static final Logger LOG = LoggerFactory.getLogger(PersonListView.class);

    private final JpaEventDispatcher eventDispatcher;

    public PersonListView() {
        eventDispatcher = new SimpleJpaEventDispatcher(
                new PersonCreatedEventHandler(), new PersonDeletedEventHandler()
        );
    }

    @Override
    public String getName() {
        return "spring-qry-personlist";
    }

    @Override
    public String getCron() {
        // Every second
        return "* * * * * *";
    }

    @Override
    public Set<EventType> getEventTypes() {
        return Set.of(PersonCreatedEvent.TYPE, PersonDeletedEvent.TYPE);
    }

    @Override
    public void handleEvents(final EntityManager em, final List<Event> events) {
        eventDispatcher.dispatchEvents(em, events);
    }

}
