package org.fuin.cqrs4j.example.quarkus.command.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.fuin.cqrs4j.ResultType;
import org.fuin.cqrs4j.SimpleResult;
import org.fuin.cqrs4j.example.shared.CreatePersonCommand;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.esc.api.CommonEvent;
import org.fuin.esc.api.SimpleStreamId;
import org.fuin.esc.api.StreamEventsSlice;
import org.fuin.esc.api.TypeName;
import org.fuin.esc.esgrpc.IESGrpcEventStore;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class PersonResourceIT {

    @Inject
    IESGrpcEventStore eventStore;

    @Inject
    Jsonb jsonb;

    @Test
    void testCreate() {

        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Peter Parker");
        final CreatePersonCommand cmd = new CreatePersonCommand.Builder().id(personId).name(personName).build();
        final String json = jsonb.toJson(cmd);

        // TEST & VERIFY
        final SimpleResult result =
                given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(json)
                .when()
                    .post("/persons/create")
                .then()
                    .statusCode(200)
                    .extract().as(SimpleResult.class);
        assertThat(result.getType()).isEqualTo(ResultType.OK);

        final SimpleStreamId personStreamId = new SimpleStreamId(PersonId.TYPE + "-" + personId);
        final StreamEventsSlice slice = eventStore.readEventsForward(personStreamId, 0, 1);
        final List<CommonEvent> events = slice.getEvents();
        assertThat(events).isNotEmpty();
        final CommonEvent ce = events.get(0);
        assertThat(ce.getDataType()).isEqualTo(new TypeName(PersonCreatedEvent.TYPE.asBaseType()));
        final PersonCreatedEvent event = (PersonCreatedEvent) ce.getData();
        assertThat(event.getEntityId()).isEqualTo(personId);
        assertThat(event.getAggregateVersionInteger()).isEqualTo(0);
        assertThat(event.getName()).isEqualTo(personName);

    }

    @Test
    void testDelete() {

        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Delete Me");
        final CreatePersonCommand createCmd = new CreatePersonCommand.Builder().id(personId).name(personName).build();
        final String jsonCreateCmd = jsonb.toJson(createCmd);
        final SimpleResult createResult =
                given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                .body(jsonCreateCmd)
                    .when()
                    .post("/persons/create")
                .then()
                    .statusCode(200)
                    .extract().as(SimpleResult.class);
        assertThat(createResult.getType()).isEqualTo(ResultType.OK);

        final DeletePersonCommand deleteCmd = new DeletePersonCommand.Builder().id(personId).name(personName).version(0).build();
        final String jsonDeleteCmd = jsonb.toJson(deleteCmd);

        // TEST & VERIFY
        final SimpleResult deleteResult =
                given()
                   .accept(ContentType.JSON)
                   .contentType(ContentType.JSON)
                   .body(jsonDeleteCmd)
                .when()
                   .pathParam("personId", personId.asString())
                   .delete("/persons/{personId}")
                .then()
                   .statusCode(200)
                   .extract().as(SimpleResult.class);
        assertThat(deleteResult.getType()).isEqualTo(ResultType.OK);

        final SimpleStreamId personStreamId = new SimpleStreamId(PersonId.TYPE + "-" + personId);
        final StreamEventsSlice slice = eventStore.readEventsForward(personStreamId, 1, 1);
        final List<CommonEvent> events = slice.getEvents();
        assertThat(events).isNotEmpty();
        final CommonEvent ce = events.get(0);
        assertThat(ce.getDataType()).isEqualTo(new TypeName(PersonDeletedEvent.TYPE.asBaseType()));
        final PersonDeletedEvent event = (PersonDeletedEvent) ce.getData();
        assertThat(event.getEntityId()).isEqualTo(personId);
        assertThat(event.getAggregateVersionInteger()).isEqualTo(1);
        assertThat(event.getName()).isEqualTo(personName);

    }

}