package org.fuin.cqrs4j.example.quarkus.query.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class QryScheduler {

    @Inject
    Event<QryCheckForViewUpdatesEvent> checkForViewUpdates;

    @Scheduled(every = "1s")
    public void fireCheckForUpdatesEvent() {
        checkForViewUpdates.fireAsync(new QryCheckForViewUpdatesEvent());
    }

}
