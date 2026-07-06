package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.View;
import org.fuin.cqrs4j.example.quarkus.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.quarkus.shared.PersonDeletedEvent;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.fuin.objects4j.common.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Maintains the "per entity type" statistic by selecting the <b>concrete</b> domain events (creation and
 * deletion) and counting the live instances per entity type. This is the event-type based counterpart of
 * {@link StatisticViewCategories}. Discovered by the library's {@code QuarkusViewManager}
 * (cqrs-4-java-quarkus-query) as a {@code @Named} CDI bean implementing {@link View}.
 */
@ThreadSafe
@Dependent
@Named(StatisticViewEvents.BEAN_NAME)
public class StatisticViewEvents implements View {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticViewEvents.class);

    /** Unique name of the view / projection. */
    public static final String NAME = "quarkus-qry-statistic-events";

    /** Name of the CDI bean. */
    public static final String BEAN_NAME = "StatisticViewEvents";

    private static final EntityType PERSON = new EntityType("person");

    private final EntityManager em;

    /**
     * Constructor with the injected entity manager.
     *
     * @param em Entity manager used to store the read model.
     */
    @Inject
    public StatisticViewEvents(final EntityManager em) {
        this.em = em;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getBeanName() {
        return BEAN_NAME;
    }

    @Override
    public Class<? extends View> getBeanClass() {
        return StatisticViewEvents.class;
    }

    @Override
    public Set<EventType> getEventTypes() {
        // Selects the concrete event types (as opposed to StatisticViewCategories, which selects by category).
        return Set.of(PersonCreatedEvent.TYPE, PersonDeletedEvent.TYPE);
    }

    @Override
    public Set<Class<?>> getEventCategories() {
        // This view selects purely by event type (see getEventTypes) - no categories.
        return Set.of();
    }

    @Override
    public String getCron() {
        // Every second (used only in poll mode; push mode subscribes instead)
        return "* * * * * ?";
    }

    @Override
    public void handleEvents(final List<Event> events) {
        for (final Event event : events) {
            if (event instanceof PersonCreatedEvent ev) {
                handlePersonCreatedEvent(ev);
            } else if (event instanceof PersonDeletedEvent ev) {
                handlePersonDeletedEvent(ev);
            } else {
                throw new IllegalStateException("Cannot handle event: " + event);
            }
        }
    }

    private void handlePersonCreatedEvent(final PersonCreatedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final StatisticEventsEntity entity = em.find(StatisticEventsEntity.class, PERSON.name());
        if (entity == null) {
            em.persist(new StatisticEventsEntity(PERSON));
        } else {
            entity.inc();
        }
    }

    private void handlePersonDeletedEvent(final PersonDeletedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final StatisticEventsEntity entity = em.find(StatisticEventsEntity.class, PERSON.name());
        if (entity != null) {
            entity.dec();
        }
    }

}
