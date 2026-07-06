package org.fuin.cqrs4j.example.spring.command.app;

import org.fuin.cqrs4j.example.spring.command.domain.EventStorePersonRepository;
import org.fuin.cqrs4j.example.spring.command.domain.PersonRepository;
import org.fuin.cqrs4j.example.spring.shared.EventstoreConfig;
import org.fuin.esc.api.EventStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

@SpringBootApplication(scanBasePackages = {
        "org.fuin.cqrs4j.example.spring.shared",
        "org.fuin.cqrs4j.example.spring.command.app",
        "org.fuin.cqrs4j.example.spring.command.controller"
})
@EnableConfigurationProperties(EventstoreConfig.class)
// Entity packages are only used under the "esc-jpa" backend-portability profile (which activates JPA); with
// the default KurrentDB backend the datasource auto-configuration is excluded and these are inert.
@EntityScan({"org.fuin.esc.jpa", "org.fuin.cqrs4j.example.spring.shared.jpa"})
public class CmdApplication {

    /**
     * Creates an event sourced repository that can store a person. Depends on the {@link EventStore} SPI, so
     * either backend (KurrentDB gRPC or the relational {@code esc-jpa} store) can be injected unchanged.
     *
     * @param eventStore
     *            Event store to use.
     *
     * @return Repository only valid for the current request.
     */
    @Bean
    @RequestScope
    public PersonRepository create(final EventStore eventStore) {
        return new EventStorePersonRepository(eventStore);
    }

    public static void main(String[] args) {
        SpringApplication.run(CmdApplication.class, args);
    }

}
