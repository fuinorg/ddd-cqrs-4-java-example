package org.fuin.cqrs4j.example.spring.query.views.statistics;

import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.View;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.fuin.ddd4j.core.ExodusEvent;
import org.fuin.ddd4j.core.GenesisEvent;
import org.fuin.objects4j.common.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * Maintains the "per category" statistic by selecting events via their {@link org.fuin.ddd4j.core.EventCategory}
 * marker interfaces and counting them per category. It deliberately does <b>not</b> reference any concrete event
 * type: a creation is any {@link GenesisEvent} and a deletion is any {@link ExodusEvent}, so new aggregates/events
 * are counted automatically without changing this view. This is the category based counterpart of
 * {@link StatisticViewEvents}. Discovered by the query-starter's {@code SpringViewManager} as a prototype-scoped
 * bean; a fresh instance (with an injected {@link EntityManager}) is created per projection run.
 */
@ThreadSafe
@Component(StatisticViewCategories.BEAN_NAME)
@Scope(SCOPE_PROTOTYPE)
public class StatisticViewCategories implements View {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticViewCategories.class);

    /** Unique name of the view / projection. */
    public static final String NAME = "spring-qry-statistic-categories";

    /** Name of the Spring bean. */
    public static final String BEAN_NAME = "StatisticViewCategories";

    /** Category name counted for every {@link GenesisEvent}. */
    public static final String CREATED = "created";

    /** Category name counted for every {@link ExodusEvent}. */
    public static final String DELETED = "deleted";

    private final EntityManager em;

    /**
     * Constructor with the injected entity manager.
     *
     * @param em Entity manager used to store the read model.
     */
    public StatisticViewCategories(final EntityManager em) {
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
        return StatisticViewCategories.class;
    }

    @Override
    public Set<EventType> getEventTypes() {
        // This view selects purely by category (see getEventCategories) - no exact event types.
        return Set.of();
    }

    @Override
    public Set<Class<?>> getEventCategories() {
        // React to every entity lifecycle event across aggregates - creation (Genesis) and hard delete
        // (Exodus) - without listing each concrete event type. New Genesis/Exodus events are picked up
        // automatically.
        return Set.of(GenesisEvent.class, ExodusEvent.class);
    }

    @Override
    public String getCron() {
        // Every second
        return "* * * * * *";
    }

    @Override
    public void handleEvents(final List<Event> events) {
        for (final Event event : events) {
            // Count purely by category marker interface - no concrete event type is referenced.
            if (event instanceof GenesisEvent) {
                inc(CREATED);
            } else if (event instanceof ExodusEvent) {
                inc(DELETED);
            } else {
                throw new IllegalStateException("Cannot handle event: " + event);
            }
        }
    }

    private void inc(final String category) {
        LOG.info("Count category '{}'", category);
        final StatisticCategoriesEntity entity = em.find(StatisticCategoriesEntity.class, category);
        if (entity == null) {
            em.persist(new StatisticCategoriesEntity(category));
        } else {
            entity.inc();
        }
    }

}
