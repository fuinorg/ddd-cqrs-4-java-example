package org.fuin.cqrs4j.example.spring.query.e2e;

import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.example.spring.command.domain.EventStorePersonRepository;
import org.fuin.cqrs4j.example.spring.command.domain.Person;
import org.fuin.cqrs4j.example.spring.command.domain.PersonRepository;
import org.fuin.cqrs4j.example.spring.query.app.QryApplication;
import org.fuin.cqrs4j.example.spring.query.views.personlist.PersonListEntry;
import org.fuin.cqrs4j.example.spring.shared.PersonId;
import org.fuin.cqrs4j.example.spring.shared.PersonName;
import org.fuin.esc.api.EventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Backend-portability integration test - the <b>same</b> round trip as {@link PersonE2EIT}, but with the
 * event store swapped from KurrentDB (gRPC) to the relational {@code esc-jpa} backend via the {@code esc-jpa}
 * Spring profile. Nothing in the {@code Person} aggregate, the event-sourced repository, the projection views
 * or the REST layer changes - only the backend behind the {@code EventStore} SPI. On this backend the query
 * side's projection catches up by <b>polling</b> the relational store (a projection is a type filter over the
 * global event log) instead of a live gRPC subscription.
 * <p>
 * Like {@link PersonE2EIT} it drives the command domain in-process against the query application's own
 * event-store connection, because the two services integrate only through the shared event store. Appends
 * are transactional through the {@code esc-jpa} event store bean (see {@code EscJpaConfig}), so no explicit
 * transaction management is needed here.
 */
// The two E2E ITs run a background projection view manager against the shared MariaDB. Close the context
// after the class so only one view manager is ever polling the database at a time (avoids the two contexts
// clobbering each other's shared projection position rows).
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = QryApplication.class)
@ActiveProfiles("esc-jpa")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PersonJpaBackendE2EIT {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext wac;

    /** The query application's own event-store connection - here the relational esc-jpa store. */
    @Autowired
    EventStore eventStore;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssuredMockMvc.webAppContextSetup(wac);
    }

    private boolean findPerson(final PersonId personId) {
        return em.find(PersonListEntry.class, personId.asString()) != null;
    }

    @Test
    void commandCreateIsProjectedToQuery_thenDeleteRemovesIt() throws Exception {

        // ---- COMMAND SIDE: create the person via the real aggregate + event-sourced repository ----
        final PersonRepository repo = new EventStorePersonRepository(eventStore);
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Gwen Stacy");
        final Person person = new Person(personId, personName, name -> Optional.empty());
        repo.add(person); // appends PersonCreatedEvent to the relational event store

        // ---- QUERY SIDE: the polling projection catches up; the person appears in the read model + REST ----
        await().atMost(15, SECONDS).until(() -> findPerson(personId));

        final PersonListEntry byId = given()
                .pathParam("id", personId.asString())
                .when().get("/persons/{id}")
                .then().statusCode(200)
                .extract().as(PersonListEntry.class);
        assertThat(byId.getId()).isEqualTo(personId);
        assertThat(byId.getName()).isEqualTo(personName);

        final PersonListEntry[] all = given()
                .when().get("/persons")
                .then().statusCode(200)
                .extract().as(PersonListEntry[].class);
        assertThat(all).anyMatch(p -> p.getId().equals(personId));

        // the statistics projection is served too
        given().when().get("/statistics").then().statusCode(200);

        // ---- COMMAND SIDE: delete the person via the aggregate ----
        final Person loaded = repo.read(personId);
        loaded.delete();
        repo.update(loaded); // appends PersonDeletedEvent

        // ---- QUERY SIDE: the projection removes it from the read model ----
        await().atMost(15, SECONDS).until(() -> !findPerson(personId));
        given()
                .pathParam("id", personId.asString())
                .when().get("/persons/{id}")
                .then().statusCode(404);
    }
}
