package org.fuin.cqrs4j.example.quarkus.query.views.personlist;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonDeletedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.esc.api.CommonEvent;
import org.fuin.esc.api.EventId;
import org.fuin.esc.api.SimpleCommonEvent;
import org.fuin.esc.api.SimpleStreamId;
import org.fuin.esc.api.TypeName;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
class PersonListResourceIT {

    @Inject
    IESGrpcEventStore eventStore;

    @Inject
    EntityManager em;

    @Test
    void testGetByIdNotFound() {
        given().pathParam("id", UUID.randomUUID()).when().get("/persons/{id}").then().statusCode(404);
    }

    @ActivateRequestContext
    public boolean findPerson(final PersonId personId) {
        return em.find(PersonListEntry.class, personId.asString()) != null;
    }

    @Test
    void testGetByIdOK() {

        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Peter Parker");
        final SimpleStreamId personStreamId = new SimpleStreamId(PersonId.TYPE + "-" + personId);
        final PersonCreatedEvent event = new PersonCreatedEvent.Builder().id(personId).name(personName).version(0).build();
        final CommonEvent ce = new SimpleCommonEvent(new EventId(event.getEventId().asBaseType()),
                new TypeName(event.getEventType().asBaseType()), event);
        eventStore.appendToStream(personStreamId, ce);

        await().atMost(5, SECONDS).until(() -> findPerson(personId));

        // TEST & VERIFY

        final PersonListEntry person =
                given()
                    .pathParam("id", personId.asString())
                .when()
                    .get("/persons/{id}")
                .then()
                    .statusCode(200)
                    .extract().as(PersonListEntry.class);
        assertThat(person.getId()).isEqualTo(personId);
        assertThat(person.getName()).isEqualTo(personName);

        final PersonListEntry[] persons =
                given()
                .when()
                    .get("/persons")
                .then()
                    .statusCode(200)
                    .extract().as(PersonListEntry[].class);

        assertThat(persons).isNotEmpty();
        final PersonListEntry person0 = persons[0];
        assertThat(person0.getId()).isEqualTo(personId);
        assertThat(person0.getName()).isEqualTo(personName);

    }

    @Test
    void testDelete() {

        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Delete Me Man");
        final SimpleStreamId personStreamId = new SimpleStreamId(PersonId.TYPE + "-" + personId);

        final PersonCreatedEvent createdEvent = new PersonCreatedEvent.Builder().id(personId).name(personName).version(0).build();
        final CommonEvent commonCreatedEvent = new SimpleCommonEvent(new EventId(createdEvent.getEventId().asBaseType()),
                new TypeName(createdEvent.getEventType().asBaseType()), createdEvent);
        eventStore.appendToStream(personStreamId, commonCreatedEvent);
        await().atMost(5, SECONDS).until(() -> findPerson(personId));

        final PersonDeletedEvent  deletedEvent = new PersonDeletedEvent.Builder().id(personId).name(personName).version(0).build();
        final CommonEvent commonDeletedEvent = new SimpleCommonEvent(new EventId(deletedEvent.getEventId().asBaseType()),
                new TypeName(deletedEvent.getEventType().asBaseType()), deletedEvent);
        eventStore.appendToStream(personStreamId, commonDeletedEvent);
        await().atMost(5, SECONDS).until(() -> !findPerson(personId));

        // TEST & VERIFY
        given()
            .pathParam("id", personId.asString())
        .when()
            .get("/persons/{id}")
        .then()
            .statusCode(404);

        final PersonListEntry[] persons =
                given()
                .when()
                    .get("/persons")
                .then()
                   .statusCode(200)
                   .extract().as(PersonListEntry[].class);

        assertThat(persons).doesNotContain(new PersonListEntry(personId, personName));

    }

}
