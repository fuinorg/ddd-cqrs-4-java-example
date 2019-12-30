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

import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.fuin.esc.eshttp.ESEnvelopeType;
import org.fuin.esc.eshttp.ESHttpEventStore;
import org.fuin.esc.eshttp.IESHttpEventStore;
import org.fuin.esc.esjc.ESJCEventStore;
import org.fuin.esc.esjc.IESJCEventStore;
import org.fuin.esc.spi.EnhancedMimeType;
import org.fuin.esc.spi.SerDeserializerRegistry;

/**
 * CDI factory that creates an event store connection and repositories.
 */
@ApplicationScoped
public class EventStoreFactory {

    /**
     * Creates an ESJC event store.<br>
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
    public IESJCEventStore createEventStore(final com.github.msemys.esjc.EventStore es,
            final SerDeserializerRegistry registry) {

        final IESJCEventStore eventstore = new ESJCEventStore(es, registry, registry,
                EnhancedMimeType.create("application", "json", Charset.forName("utf-8")));
        eventstore.open();
        return eventstore;

    }

    /**
     * Closes the ESJC event store when the context is disposed.
     * 
     * @param es Event store to close.
     */
    public void closeEventStore(@Disposes final IESJCEventStore es) {
        es.close();
    }

    /**
     * Creates an HTTP event store.<br>
     * <br>
     * CAUTION: The returned event store instance is NOT thread safe.
     * 
     * @param config
     *            Configuration.
     * @param registry
     *            Serialization registry.
     * 
     * @return Dependent scope event store.
     */
    @Produces
    @Dependent
    public IESHttpEventStore createEventStore(final Config config, final SerDeserializerRegistry registry) {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(config.getEventStoreUser(),
                config.getEventStorePassword());
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        final IESHttpEventStore eventStore = new ESHttpEventStore(threadFactory, config.getEventStoreURL(), ESEnvelopeType.JSON, registry,
                registry, credentialsProvider);
        eventStore.open();
        return eventStore;

    }

    /**
     * Closes the HTTP event store when the context is disposed.
     * 
     * @param es
     *            Event store to close.
     */
    public void closeEventStore(@Disposes final IESHttpEventStore es) {
        es.close();
    }
    
}
