package org.fuin.cqrs4j.example.spring.query.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.spring.shared.SharedUtils;
import org.fuin.esc.eshttp.ESEnvelopeType;
import org.fuin.esc.eshttp.ESHttpEventStore;
import org.fuin.esc.eshttp.IESHttpEventStore;
import org.fuin.esc.spi.SerDeserializerRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = { "org.fuin.cqrs4j.example.spring.query.app",
		"org.fuin.cqrs4j.example.spring.query.controller", "org.fuin.cqrs4j.example.spring.query.domain",
		"org.fuin.cqrs4j.example.spring.query.handler" })
@EnableJpaRepositories("org.fuin.cqrs4j.example.spring.query.domain")
@EntityScan({ "org.fuin.cqrs4j.example.spring.query.domain", "org.fuin.cqrs4j.example.spring.query.handler" })
@EnableScheduling
@EnableAsync
public class QryApplication {

	/**
	 * Creates a Jsonb instance.
	 * 
	 * @return Fully configured instance.
	 */
	@Bean
	public Jsonb createJsonb() {
		final JsonbConfig config = new JsonbConfig().withAdapters(SharedUtils.JSONB_ADAPTERS)
				.withPropertyVisibilityStrategy(new FieldAccessStrategy());
		final Jsonb jsonb = JsonbBuilder.create(config);
		return jsonb;
	}

	/**
	 * Creates a HTTP based event store connection.
	 * 
	 * @param config Configuration to use.
	 * 
	 * @return New event store instance.
	 */
	@Bean(destroyMethod = "close")
	public IESHttpEventStore getESHttpEventStore(final QryConfig config) {
		final String url = config.getEventStoreProtocol() + "://" + config.getEventStoreHost() + ":"
				+ config.getEventStoreHttpPort();
		try {
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(config.getEventStoreUser(),
					config.getEventStorePassword());
			credentialsProvider.setCredentials(AuthScope.ANY, credentials);
			final SerDeserializerRegistry registry = SharedUtils.createRegistry();
			final ESHttpEventStore es = new ESHttpEventStore(Executors.defaultThreadFactory(), new URL(url),
					ESEnvelopeType.JSON, registry, registry, credentialsProvider);
			es.open();
			return es;
		} catch (final MalformedURLException ex) {
			throw new RuntimeException("Failed to create URL: " + url, ex);
		}
	}

	@Bean("personProjectorExecutor")
	public Executor taskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("person-");
		executor.initialize();
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(QryApplication.class, args);
	}

}
