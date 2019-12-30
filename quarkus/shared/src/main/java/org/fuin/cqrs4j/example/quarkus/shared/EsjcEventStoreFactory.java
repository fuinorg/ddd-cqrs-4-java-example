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
package org.fuin.cqrs4j.example.quarkus.shared;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.context.ManagedExecutor;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;

/**
 * CDI producer that creates a {@link EventStore}.
 */
@ApplicationScoped
public class EsjcEventStoreFactory {

    @Produces
    @ApplicationScoped
    public EventStore createESJC(final ManagedExecutor executor, final Config config) {
        return EventStoreBuilder.newBuilder().singleNodeAddress(config.getEventStoreHost(), config.getEventStoreTcpPort())
                .executor(executor).userCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .build();
    }

}