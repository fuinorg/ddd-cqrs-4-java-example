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
package org.fuin.cqrs4j.example.javasecdi.shared.app;

import java.util.concurrent.Executors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;

/**
 * CDI producer that creates a {@link EventStore}.
 */
@ApplicationScoped
public class SharedEsjcEventStoreFactory {

    @Produces
    @ApplicationScoped
    public EventStore createESJC(final SharedConfig config) {
        return EventStoreBuilder.newBuilder().singleNodeAddress(config.getEventStoreHost(), config.getEventStoreTcpPort())
                .executor(Executors.newFixedThreadPool(10)).userCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .build();
    }

}