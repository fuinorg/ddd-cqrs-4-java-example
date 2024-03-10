package org.fuin.cqrs4j.example.shared;

import org.fuin.ddd4j.core.Event;
import org.fuin.ddd4j.core.EventType;

import java.util.List;
import java.util.Set;

/**
 * Defines a unit that projects events read from the event store into another representation.
 * The view is updated regularly by using a scheduler.
 */
public interface View {

    /**
     * Unique name of the view.
     *
     * @return Name that is unique in this program instance.
     */
    String getName();

    /**
     * Returns the CRON expression defining how often the view should be updated.
     *
     * @return Spring Quartz CRON expression
     */
    String getCron();

    /**
     * Returns the type of events the view is interested in.
     *
     * @return List of events.
     */
    Set<EventType> getEventTypes();

    /**
     * Number of events to read and handle in one transaction.
     *
     * @return Number of events (defaults to 100).
     */
    default int getChunkSize() {
        return 100;
    }

    /**
     * Events to handle by the view.
     *
     * @param events Events used to update the view.
     */
    void handleEvents(List<Event> events);

}
