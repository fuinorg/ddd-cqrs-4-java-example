package org.fuin.cqrs4j.example.javasecdi.qry.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class QryTimer implements Runnable {

    @Inject
    private Event<QryCheckForViewUpdatesEvent> checkForViewUpdates;

    /**
     * Notifies all listeners to check for view updates.
     */
    public void run() {
        checkForViewUpdates.fireAsync(new QryCheckForViewUpdatesEvent());
    }

}
