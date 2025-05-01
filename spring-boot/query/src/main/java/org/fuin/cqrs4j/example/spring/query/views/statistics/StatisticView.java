package org.fuin.cqrs4j.example.spring.query.views.statistics;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.JpaView;
import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Handles the events required to maintain the statistic database view.
 */
public class StatisticView implements JpaView {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticView.class);

    private static final EntityType PERSON = new EntityType("person");

    @Override
    public String getName() {
        return "spring-qry-statistic";
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
        for (final Event event : events) {
            if (event instanceof PersonCreatedEvent ev) {
                handlePersonCreatedEvent(em, ev);
            } else if (event instanceof PersonDeletedEvent ev) {
                handlePersonDeletedEvent(em, ev);
            } else {
                throw new IllegalStateException("Cannot handle event: " + event);
            }
        }
    }

    private void handlePersonCreatedEvent(final EntityManager em, final PersonCreatedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final StatisticEntity entity = em.find(StatisticEntity.class, PERSON.name());
        if (entity == null) {
            em.persist(new StatisticEntity(PERSON));
        } else {
            entity.inc();
        }
    }

    private void handlePersonDeletedEvent(final EntityManager em, final PersonDeletedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final StatisticEntity entity = em.find(StatisticEntity.class, PERSON.name());
        if (entity != null) {
            entity.dec();
        }
    }

}