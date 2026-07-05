package org.fuin.cqrs4j.example.spring.query.views.statistics;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.View;
import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.fuin.objects4j.common.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * Maintains the statistic database view. Discovered by the query-starter's {@code SpringViewManager} as a
 * prototype-scoped bean; a fresh instance (with an injected {@link EntityManager}) is created per projection run.
 */
@ThreadSafe
@Component(StatisticView.BEAN_NAME)
@Scope(SCOPE_PROTOTYPE)
public class StatisticView implements View {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticView.class);

    /** Unique name of the view / projection. */
    public static final String NAME = "spring-qry-statistic";

    /** Name of the Spring bean. */
    public static final String BEAN_NAME = "StatisticView";

    private static final EntityType PERSON = new EntityType("person");

    private final EntityManager em;

    /**
     * Constructor with the injected entity manager.
     *
     * @param em Entity manager used to store the read model.
     */
    public StatisticView(final EntityManager em) {
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
        return StatisticView.class;
    }

    @Override
    public Set<EventType> getEventTypes() {
        return Set.of(PersonCreatedEvent.TYPE, PersonDeletedEvent.TYPE);
    }

    @Override
    public String getCron() {
        // Every second
        return "* * * * * *";
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
