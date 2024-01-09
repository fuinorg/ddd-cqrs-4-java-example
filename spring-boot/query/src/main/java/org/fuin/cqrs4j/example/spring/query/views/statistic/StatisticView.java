package org.fuin.cqrs4j.example.spring.query.views.statistic;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.shared.View;
import org.fuin.ddd4j.ddd.Event;
import org.fuin.ddd4j.ddd.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Handles the events required to maintain the statistic database view.
 */
@Component
public class StatisticView implements View {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticView.class);

    private static final EntityType PERSON = new EntityType("person");

    private final EntityManager em;

    public StatisticView(EntityManager em) {
        this.em = Objects.requireNonNull(em, "em==null");
    }

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
    public void handleEvents(final List<Event> events) {
        for (final Event event : events) {
            if (event instanceof PersonCreatedEvent ev) {
                handlePersonCreatedEvent(ev);
            } else if (event instanceof PersonDeletedEvent ev) {
                    handlePersonDeletedEvent(ev);
            } else {
                throw new RuntimeException("Cannot handle event: " + event);
            }
        }
    }

    private void handlePersonCreatedEvent(final PersonCreatedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final StatisticEntity entity = em.find(StatisticEntity.class, PERSON.name());
        if (entity == null) {
            em.persist(new StatisticEntity(PERSON));
        } else {
            entity.inc();
        }
    }

    private void handlePersonDeletedEvent(final PersonDeletedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final StatisticEntity entity = em.find(StatisticEntity.class, PERSON.name());
        if (entity != null) {
            entity.dec();
        }
    }

}