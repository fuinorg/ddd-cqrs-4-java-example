package org.fuin.cqrs4j.example.quarkus.query.app;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class QryScheduler {

    @Inject
    Event<QryCheckForViewUpdatesEvent> checkForViewUpdates;

    @Scheduled(every = "1s")
    public void fireCheckForUpdatesEvent() {
        checkForViewUpdates.fireAsync(new QryCheckForViewUpdatesEvent());
    }

}
