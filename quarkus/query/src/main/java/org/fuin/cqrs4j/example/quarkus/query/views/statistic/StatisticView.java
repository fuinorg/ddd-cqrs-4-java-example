package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.example.shared.View;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.ddd4j.ddd.Event;
import org.fuin.ddd4j.ddd.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Handles the events required to maintain the statistic database view.
 */
@ApplicationScoped
public class StatisticView implements View {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticView.class);

    private static final EntityType PERSON = new EntityType("person");

    @Inject
    EntityManager em;

    @Override
    public String getName() {
        return "quarkus-qry-statistic";
    }

    @Override
    public String getCron() {
        // Every minute
        return "* * * * * ? *";
    }

    @Override
    public Set<EventType> getEventTypes() {
        return Set.of(PersonCreatedEvent.TYPE);
    }

    @Override
    public void handleEvents(final List<Event> events) {
        for (final Event event : events) {
            if (event instanceof PersonCreatedEvent ev) {
                handlePersonCreatedEvent(ev);
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

}