package org.fuin.cqrs4j.example.spring.command.app;

import org.fuin.cqrs4j.example.aggregates.PersonRepository;
import org.fuin.esc.esjc.IESJCEventStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

@SpringBootApplication(scanBasePackages = { "org.fuin.cqrs4j.example.spring.command.app",
		"org.fuin.cqrs4j.example.spring.command.controller", "org.fuin.cqrs4j.example.spring.shared" })
public class CmdApplication {

	/**
	 * Creates an event sourced repository that can store a person.
	 * 
	 * @param eventStore Event store to use.
	 * 
	 * @return Repository only valid for the current request.
	 */
	@Bean
	@RequestScope
	public PersonRepository create(final IESJCEventStore eventStore) {
		return new PersonRepository(eventStore);
	}

	public static void main(String[] args) {
		SpringApplication.run(CmdApplication.class, args);
	}

}
