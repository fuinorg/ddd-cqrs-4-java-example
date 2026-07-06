package org.fuin.cqrs4j.example.quarkus.query.views.statistic;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.View;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.fuin.ddd4j.core.ExodusEvent;
import org.fuin.ddd4j.core.GenesisEvent;
import org.fuin.objects4j.common.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Maintains the "per category" statistic by selecting events via their {@link org.fuin.ddd4j.core.EventCategory}
 * marker interfaces and counting them per category. It deliberately does <b>not</b> reference any concrete event
 * type: a creation is any {@link GenesisEvent} and a deletion is any {@link ExodusEvent}, so new aggregates/events
 * are counted automatically without changing this view. This is the category based counterpart of
 * {@link StatisticViewEvents}. Discovered by the library's {@code QuarkusViewManager}
 * (cqrs-4-java-quarkus-query) as a {@code @Named} CDI bean implementing {@link View}.
 */
@ThreadSafe
@Dependent
@Named(StatisticViewCategories.BEAN_NAME)
public class StatisticViewCategories implements View {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticViewCategories.class);

    /** Unique name of the view / projection. */
    public static final String NAME = "quarkus-qry-statistic-categories";

    /** Name of the CDI bean. */
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
    @Inject
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
        // Every second (used only in poll mode; push mode subscribes instead)
        return "* * * * * ?";
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
