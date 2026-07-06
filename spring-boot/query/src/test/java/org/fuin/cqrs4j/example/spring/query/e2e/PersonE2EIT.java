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
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * End-to-end demo test — the full CQRS/Event-Sourcing round trip the command and query microservices
 * integrate through.
 * <p>
 * The command side creates/deletes a person through its <em>real</em> {@code Person} aggregate and
 * event-sourced {@link EventStorePersonRepository} (appending {@code PersonCreatedEvent} /
 * {@code PersonDeletedEvent} to the {@code PERSON-<id>} stream). The query side's projection consumes
 * those events into the JPA read model, which is served over REST. The two services never call each
 * other directly — they integrate <b>only through the shared event store</b> — so this in-process test
 * drives the command domain's write path against the query app's event-store connection to prove the
 * whole pipeline: <code>command aggregate → event store → query projection → query REST</code>.
 * <p>
 * The manual, cross-process (and cross-stack Quarkus&lt;-&gt;Spring) walkthrough is
 * {@code doc/demo-e2e.md}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = QryApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PersonE2EIT {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext wac;

    /** The query application's own event-store connection — the integration seam between the services. */
    @Autowired
    IESGrpcEventStore eventStore;

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
        repo.add(person); // appends PersonCreatedEvent to the event store

        // ---- QUERY SIDE: the projection catches up; the person appears in the read model + REST ----
        await().atMost(10, SECONDS).until(() -> findPerson(personId));

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

        // both statistics projections (event-type based and category based) are served too
        given().when().get("/statistics-events").then().statusCode(200);
        given().when().get("/statistics-categories").then().statusCode(200);

        // ---- COMMAND SIDE: delete the person via the aggregate ----
        final Person loaded = repo.read(personId);
        loaded.delete();
        repo.update(loaded); // appends PersonDeletedEvent

        // ---- QUERY SIDE: the projection removes it from the read model ----
        await().atMost(10, SECONDS).until(() -> !findPerson(personId));
        given()
                .pathParam("id", personId.asString())
                .when().get("/persons/{id}")
                .then().statusCode(404);
    }
}
