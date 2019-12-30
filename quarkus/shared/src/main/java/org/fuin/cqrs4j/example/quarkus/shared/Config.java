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

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Application configuration.
 */
@ApplicationScoped
public class Config {

    private static final String EVENT_STORE_HOST = "127.0.0.1";

    private static final int EVENT_STORE_HTTP_PORT = 2113;

    private static final int EVENT_STORE_TCP_PORT = 1113;

    private static final String EVENT_STORE_USER = "admin";

    private static final String EVENT_STORE_PASSWORD = "changeit";

    @Inject
    @ConfigProperty(name = "EVENT_STORE_HOST", defaultValue = EVENT_STORE_HOST)
    String eventStoreHost;

    @Inject
    @ConfigProperty(name = "EVENT_STORE_HTTP_PORT", defaultValue = "" + EVENT_STORE_HTTP_PORT)
    int eventStoreHttpPort;

    @Inject
    @ConfigProperty(name = "EVENT_STORE_TCP_PORT", defaultValue = "" + EVENT_STORE_TCP_PORT)
    int eventStoreTcpPort;

    @Inject
    @ConfigProperty(name = "EVENT_STORE_USER", defaultValue = EVENT_STORE_USER)
    String eventStoreUser;

    @Inject
    @ConfigProperty(name = "EVENT_STORE_PASSWORD", defaultValue = EVENT_STORE_PASSWORD)
    String eventStorePassword;

    /**
     * Constructor using default values internally.
     */
    public Config() {
        super();
        this.eventStoreHost = EVENT_STORE_HOST;
        this.eventStoreHttpPort = EVENT_STORE_HTTP_PORT;
        this.eventStoreTcpPort = EVENT_STORE_TCP_PORT;
        this.eventStoreUser = EVENT_STORE_USER;
        this.eventStorePassword = EVENT_STORE_PASSWORD;
    }

    /**
     * Constructor with all data.
     * 
     * @param eventStoreHost     Host.
     * @param eventStoreHttpPort HTTP port
     * @param eventStoreTcpPort  TCP port.
     * @param eventStoreUser     User.
     * @param eventStorePassword Password.
     */
    public Config(final String eventStoreHost, final int eventStoreHttpPort, final int eventStoreTcpPort,
            final String eventStoreUser, final String eventStorePassword) {
        super();
        this.eventStoreHost = eventStoreHost;
        this.eventStoreHttpPort = eventStoreHttpPort;
        this.eventStoreTcpPort = eventStoreTcpPort;
        this.eventStoreUser = eventStoreUser;
        this.eventStorePassword = eventStorePassword;
    }

    /**
     * Returns the host name of the event store.
     * 
     * @return Name.
     */
    public String getEventStoreHost() {
        return eventStoreHost;
    }

    /**
     * Returns the HTTP port of the event store.
     * 
     * @return Port.
     */
    public int getEventStoreHttpPort() {
        return eventStoreHttpPort;
    }

    /**
     * Returns the TCP port of the event store.
     * 
     * @return Port.
     */
    public int getEventStoreTcpPort() {
        return eventStoreTcpPort;
    }

    /**
     * Returns the username of the event store.
     * 
     * @return Username.
     */
    public String getEventStoreUser() {
        return eventStoreUser;
    }

    /**
     * Returns the password of the event store.
     * 
     * @return Password.
     */
    public String getEventStorePassword() {
        return eventStorePassword;
    }

    /**
     * Creates a URL with parameters from the config.
     * 
     * @return Event store base URL.
     */
    public URL getEventStoreURL() {
        try {
            return new URL("http", eventStoreHost, eventStoreHttpPort, "/");
        } catch (final MalformedURLException ex) {
            throw new RuntimeException("Failed to create event store URL", ex);
        }
    }

}