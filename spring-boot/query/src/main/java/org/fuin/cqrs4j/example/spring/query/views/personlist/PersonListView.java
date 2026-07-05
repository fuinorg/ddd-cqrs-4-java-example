package org.fuin.cqrs4j.example.spring.query.views.personlist;


import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.core.View;
import org.fuin.cqrs4j.esc.JpaEventDispatcher;
import org.fuin.cqrs4j.esc.SimpleJpaEventDispatcher;
import org.fuin.cqrs4j.example.spring.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.spring.shared.PersonDeletedEvent;
import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;
import org.fuin.objects4j.common.ThreadSafe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * View with the list of persons. Discovered by the query-starter's {@code SpringViewManager} as a
 * prototype-scoped bean; a fresh instance (with an injected {@link EntityManager}) is created per projection run.
 */
@ThreadSafe
@Component(PersonListView.BEAN_NAME)
@Scope(SCOPE_PROTOTYPE)
public class PersonListView implements View {

    /** Unique name of the view / projection. */
    public static final String NAME = "spring-qry-personlist";

    /** Name of the Spring bean. */
    public static final String BEAN_NAME = "PersonListView";

    private final EntityManager em;

    private final JpaEventDispatcher eventDispatcher;

    /**
     * Constructor with the injected entity manager.
     *
     * @param em Entity manager used to store the read model.
     */
    public PersonListView(final EntityManager em) {
        this.em = em;
        this.eventDispatcher = new SimpleJpaEventDispatcher(
                new PersonCreatedEventHandler(), new PersonDeletedEventHandler()
        );
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
        // Every second
        return "* * * * * *";
    }

    @Override
    public void handleEvents(final List<Event> events) {
        eventDispatcher.dispatchEvents(em, events);
    }

}
