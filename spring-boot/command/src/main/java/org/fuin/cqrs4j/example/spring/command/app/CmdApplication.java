package org.fuin.cqrs4j.example.spring.command.app;

import org.fuin.cqrs4j.example.spring.command.domain.EventStorePersonRepository;
import org.fuin.cqrs4j.example.spring.command.domain.PersonRepository;
import org.fuin.cqrs4j.springboot.base.EventstoreConfig;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

@SpringBootApplication(scanBasePackages = {
        "org.fuin.cqrs4j.example.spring.shared",
        "org.fuin.cqrs4j.example.spring.command.app",
        "org.fuin.cqrs4j.example.spring.command.controller"
})
@EnableConfigurationProperties(EventstoreConfig.class)
public class CmdApplication {

    /**
     * Creates an event sourced repository that can store a person.
     * 
     * @param eventStore
     *            Event store to use.
     * 
     * @return Repository only valid for the current request.
     */
    @Bean
    @RequestScope
    public PersonRepository create(final IESGrpcEventStore eventStore) {
        return new EventStorePersonRepository(eventStore);
    }

    public static void main(String[] args) {
        SpringApplication.run(CmdApplication.class, args);
    }

}
