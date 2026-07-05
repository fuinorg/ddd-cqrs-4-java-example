package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.View;
import org.fuin.cqrs4j.example.quarkus.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.quarkus.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.quarkus.shared.PersonId;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.fuin.objects4j.common.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * View with the list of persons. Discovered by the library's {@code QuarkusViewManager}
 * (cqrs-4-java-quarkus-query) as a {@code @Named} CDI bean implementing {@link View}; the manager
 * creates the event store projection and dispatches matching events to {@link #handleEvents(List)}.
 */
@ThreadSafe
@Dependent
@Named(PersonListView.BEAN_NAME)
public class PersonListView implements View {

    private static final Logger LOG = LoggerFactory.getLogger(PersonListView.class);

    /** Unique name of the view / projection. */
    public static final String NAME = "quarkus-qry-personlist";

    /** Name of the CDI bean. */
    public static final String BEAN_NAME = "PersonListView";

    private final EntityManager em;

    /**
     * Constructor with the injected entity manager.
     *
     * @param em Entity manager used to store the read model.
     */
    @Inject
    public PersonListView(final EntityManager em) {
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
        return PersonListView.class;
    }

    @Override
    public Set<EventType> getEventTypes() {
        return Set.of(PersonCreatedEvent.TYPE, PersonDeletedEvent.TYPE);
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
        final PersonId personId = event.getEntityId();
        if (em.find(PersonListEntry.class, personId.asString()) == null) {
            em.persist(new PersonListEntry(personId, event.getName()));
        }
    }

    private void handlePersonDeletedEvent(final PersonDeletedEvent event) {
        LOG.info("Handle {}: {}", event.getClass().getSimpleName(), event);
        final PersonId personId = event.getEntityId();
        final PersonListEntry entity = em.find(PersonListEntry.class, personId.asString());
        if (entity != null) {
            em.remove(entity);
        }
    }

}
