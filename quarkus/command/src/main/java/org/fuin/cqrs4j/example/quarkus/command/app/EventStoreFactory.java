/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library. If not, see
 * http://www.gnu.org/licenses/.
 */
package org.fuin.cqrs4j.example.quarkus.command.app;

import java.nio.charset.Charset;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.fuin.esc.api.EventStore;
import org.fuin.esc.esjc.ESJCEventStore;
import org.fuin.esc.spi.EnhancedMimeType;
import org.fuin.esc.spi.SerDeserializerRegistry;

/**
 * CDI factory that creates an event store connection and repositories.
 */
@ApplicationScoped
public class EventStoreFactory {

    /**
     * Creates an event store.<br>
     * <br>
     * CAUTION: The returned event store instance is NOT thread safe. 
     * 
     * @param es       Native event store API.
     * @param registry Serialization registry.
     * 
     * @return Dependent scope event store.
     */    
    @Produces
    @RequestScoped
    public EventStore createEventStore(final com.github.msemys.esjc.EventStore es,
            final SerDeserializerRegistry registry) {

        final EventStore eventstore = new ESJCEventStore(es, registry, registry,
                EnhancedMimeType.create("application", "json", Charset.forName("utf-8")));
        eventstore.open();
        return eventstore;

    }

    /**
     * Closes the event store when the context is disposed.
     * 
     * @param es Event store to close.
     */
    public void closeEventStore(@Disposes final EventStore es) {
        es.close();
    }

}
