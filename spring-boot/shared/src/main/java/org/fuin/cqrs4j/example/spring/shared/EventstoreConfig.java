package org.fuin.cqrs4j.example.spring.shared;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Connection configuration for the event store.
 *
 * <p>
 * The library's own {@code org.fuin.cqrs4j.springboot.query.core.base.EventstoreConfig} was moved into the
 * query engine (query-core). To avoid dragging the whole query engine into the {@code command} and
 * {@code shared} modules just for the host/port/tls values, this app keeps its own small copy bound to the
 * same {@code org.fuin.cqrs4j.eventstore} property prefix.
 * </p>
 */
@ConfigurationProperties(EventstoreConfig.PREFIX)
public class EventstoreConfig {

    static final String PREFIX = "org.fuin.cqrs4j.eventstore";

    /** Key for the eventstore TLS property. */
    public static final String KEY_TLS = PREFIX + ".tls";

    /** Key for the eventstore host property. */
    public static final String KEY_HOST = PREFIX + ".host";

    /** Key for the eventstore port property. */
    public static final String KEY_PORT = PREFIX + ".port";

    private final boolean tls;

    @Size(min = 1, max = 235)
    private final String host;

    @Min(1024)
    @Max(65535)
    private final int port;

    /**
     * Constructor with all data.
     *
     * @param tls  Use TLS (https) or not (http).
     * @param host Host name.
     * @param port Port number.
     */
    public EventstoreConfig(final Boolean tls, final String host, final Integer port) {
        super();
        this.tls = tls != null && tls;
        this.host = host == null ? "localhost" : host;
        this.port = port == null ? 2113 : port;
    }

    /**
     * Returns if TLS should be used to communicate with the event store.
     *
     * @return {@literal true} use TLS (https) or {@literal false} (http).
     */
    public boolean isTls() {
        return tls;
    }

    /**
     * Returns the host name of the event store.
     *
     * @return Host name.
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the HTTP/HTTPS port of the event store.
     *
     * @return Port number.
     */
    public int getPort() {
        return port;
    }

}
