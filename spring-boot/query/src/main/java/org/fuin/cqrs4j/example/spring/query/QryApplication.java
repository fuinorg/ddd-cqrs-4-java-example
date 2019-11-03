package org.fuin.cqrs4j.example.spring.query;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.fuin.cqrs4j.example.spring.shared.SharedUtils;
import org.fuin.esc.eshttp.ESEnvelopeType;
import org.fuin.esc.eshttp.ESHttpEventStore;
import org.fuin.esc.eshttp.IESHttpEventStore;
import org.fuin.esc.spi.SerDeserializerRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(scanBasePackages = "org.fuin.cqrs4j.example.spring.query")
@EnableScheduling
@EnableAsync
public class QryApplication {

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
