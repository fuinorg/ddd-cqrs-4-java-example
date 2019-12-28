package org.fuin.cqrs4j.example.spring.command.app;

import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.eclipse.yasson.FieldAccessStrategy;
import org.fuin.cqrs4j.example.spring.command.domain.PersonRepository;
import org.fuin.cqrs4j.example.spring.shared.SharedUtils;
import org.fuin.esc.api.EventStore;
import org.fuin.esc.esjc.ESJCEventStore;
import org.fuin.esc.spi.EnhancedMimeType;
import org.fuin.esc.spi.SerDeserializerRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

import com.github.msemys.esjc.EventStoreBuilder;

@SpringBootApplication(scanBasePackages = "org.fuin.cqrs4j.example.spring.command")
public class CmdApplication {

	/**
	 * Creates a Jsonb instance.
	 * 
	 * @return Fully configured instance.
	 */
	@Bean
    public Jsonb createJsonb() {
        final JsonbConfig config = new JsonbConfig()
                .withAdapters(SharedUtils.JSONB_ADAPTERS)
                .withPropertyVisibilityStrategy(new FieldAccessStrategy());
        final Jsonb jsonb = JsonbBuilder.create(config);
        return jsonb;
    }
	
	/**
	 * Creates a TCP based event store connection. 
	 * 
	 * @param config Configuration to use.
	 * 
	 * @return New event store instance.
	 */	
	@Bean(destroyMethod = "shutdown")
	public com.github.msemys.esjc.EventStore getESHttpEventStore(final CmdConfig config) {
        return EventStoreBuilder.newBuilder().singleNodeAddress(config.getEventStoreHost(), config.getEventStoreTcpPort())
                .executor(Executors.newFixedThreadPool(10)).userCredentials(config.getEventStoreUser(), config.getEventStorePassword())
                .build();
	}
	
	
	/**
	 * Creates an event store connection. 
	 * 
	 * @param config Configuration to use.
	 * 
	 * @return New event store instance.
	 */	
	@Bean(destroyMethod = "close")
    public EventStore getEventStore(final com.github.msemys.esjc.EventStore es) {

		final SerDeserializerRegistry registry = SharedUtils.createRegistry();
        final EventStore eventstore = new ESJCEventStore(es, registry, registry,
                EnhancedMimeType.create("application", "json", Charset.forName("utf-8")));
        eventstore.open();
        return eventstore;

    }	
	
	/**
	 * Creates an event sourced repository that can store a person. 
	 * 
	 * @param eventStore Event store to use.
	 * 
	 * @return Repository only valid for the current request.
	 */
	@Bean
	@RequestScope
	public PersonRepository create(final EventStore eventStore) {
        return new PersonRepository(eventStore);
    }	
	
 	public static void main(String[] args) {
		SpringApplication.run(CmdApplication.class, args);
	}

}
