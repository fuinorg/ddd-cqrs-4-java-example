package org.fuin.cqrs4j.example.javasecdi.shared.app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Application configuration.
 */
@ApplicationScoped
public class SharedConfig {

	private static final String EVENT_STORE_HOST = "127.0.0.1";

	private static final int EVENT_STORE_HTTP_PORT = 2113;

	private static final String EVENT_STORE_USER = "admin";

	private static final String EVENT_STORE_PASSWORD = "changeit";

	@Inject
	@ConfigProperty(name = "EVENT_STORE_HOST", defaultValue = EVENT_STORE_HOST)
	private String eventStoreHost;

	@Inject
	@ConfigProperty(name = "EVENT_STORE_HTTP_PORT", defaultValue = "" + EVENT_STORE_HTTP_PORT)
	private int eventStoreHttpPort;

	@Inject
	@ConfigProperty(name = "EVENT_STORE_USER", defaultValue = EVENT_STORE_USER)
	private String eventStoreUser;

	@Inject
	@ConfigProperty(name = "EVENT_STORE_PASSWORD", defaultValue = EVENT_STORE_PASSWORD)
	private String eventStorePassword;

	/**
	 * Constructor using default values internally.
	 */
	public SharedConfig() {
		super();
		this.eventStoreHost = EVENT_STORE_HOST;
		this.eventStoreHttpPort = EVENT_STORE_HTTP_PORT;
		this.eventStoreUser = EVENT_STORE_USER;
		this.eventStorePassword = EVENT_STORE_PASSWORD;
	}

	/**
	 * Constructor with all data.
	 * 
	 * @param eventStoreHost     Host.
	 * @param eventStoreHttpPort HTTP port
	 * @param eventStoreUser     User.
	 * @param eventStorePassword Password.
	 */
	public SharedConfig(final String eventStoreHost, final int eventStoreHttpPort, final String eventStoreUser,
			final String eventStorePassword) {
		super();
		this.eventStoreHost = eventStoreHost;
		this.eventStoreHttpPort = eventStoreHttpPort;
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