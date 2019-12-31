package org.fuin.cqrs4j.example.spring.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

	private static final String EVENT_STORE_PROTOCOL = "http";

	private static final String EVENT_STORE_HOST = "localhost";

	private static final int EVENT_STORE_HTTP_PORT = 2113;

	private static final int EVENT_STORE_TCP_PORT = 1113;

	private static final String EVENT_STORE_USER = "admin";

	private static final String EVENT_STORE_PASSWORD = "changeit";

	@Value("${EVENT_STORE_PROTOCOL:http}")
	private String eventStoreProtocol;

	@Value("${EVENT_STORE_HOST:localhost}")
	private String eventStoreHost;

	@Value("${EVENT_STORE_HTTP_PORT:2113}")
	private int eventStoreHttpPort;

	@Value("${EVENT_STORE_TCP_PORT:1113}")
	private int eventStoreTcpPort;

	@Value("${EVENT_STORE_USER:admin}")
	private String eventStoreUser;

	@Value("${EVENT_STORE_PASSWORD:changeit}")
	private String eventStorePassword;

	/**
	 * Constructor using default values internally.
	 */
	public Config() {
		super();
		this.eventStoreProtocol = EVENT_STORE_PROTOCOL;
		this.eventStoreHost = EVENT_STORE_HOST;
		this.eventStoreHttpPort = EVENT_STORE_HTTP_PORT;
		this.eventStoreTcpPort = EVENT_STORE_TCP_PORT;
		this.eventStoreUser = EVENT_STORE_USER;
		this.eventStorePassword = EVENT_STORE_PASSWORD;
	}

	/**
	 * Constructor with all data.
	 *
	 * @param eventStoreProtocol Protocol.
	 * @param eventStoreHost     Host.
	 * @param eventStoreHttpPort HTTP port
	 * @param eventStoreTcpPort  TCP port.
	 * @param eventStoreUser     User.
	 * @param eventStorePassword Password.
	 */
	public Config(final String eventStoreProtocol, final String eventStoreHost, final int eventStoreHttpPort,
			final int eventStoreTcpPort, final String eventStoreUser, final String eventStorePassword) {
		super();
		this.eventStoreProtocol = eventStoreProtocol;
		this.eventStoreHost = eventStoreHost;
		this.eventStoreHttpPort = eventStoreHttpPort;
		this.eventStoreTcpPort = eventStoreTcpPort;
		this.eventStoreUser = eventStoreUser;
		this.eventStorePassword = eventStorePassword;
	}

	/**
	 * Returns the protocol of the event store.
	 *
	 * @return Either http or https.
	 */
	public String getEventStoreProtocol() {
		return eventStoreProtocol;
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

}
