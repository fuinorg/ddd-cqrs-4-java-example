package org.fuin.cqrs4j.example.javasecdi.qry.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

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
