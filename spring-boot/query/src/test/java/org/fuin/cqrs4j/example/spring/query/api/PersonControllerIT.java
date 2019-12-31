package org.fuin.cqrs4j.example.spring.query.api;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.cqrs4j.example.spring.query.app.QryApplication;
import org.fuin.cqrs4j.example.spring.query.controller.PersonController;
import org.fuin.cqrs4j.example.spring.query.views.personlist.PersonListEntry;
import org.fuin.cqrs4j.example.spring.shared.Config;
import org.fuin.esc.api.CommonEvent;
import org.fuin.esc.api.EventId;
import org.fuin.esc.api.SimpleCommonEvent;
import org.fuin.esc.api.SimpleStreamId;
import org.fuin.esc.api.TypeName;
import org.fuin.esc.eshttp.IESHttpEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = QryApplication.class)
public class PersonControllerIT {

	@LocalServerPort
    int port;
	
	@Autowired
	WebApplicationContext wac;
	
	@Autowired
	IESHttpEventStore eventStore;

	@Autowired
	EntityManager em;

	@Autowired
	PersonController testee;

	@Autowired
	Config config;

	@BeforeEach
	public void initRestAssuredMockMvcStandalone() {
		RestAssured.port = port;
		RestAssuredMockMvc.webAppContextSetup(wac);
	}

	@Test
	public void testGetByIdNotFound() {
		given().pathParam("id", UUID.randomUUID()).when().get("/persons/{id}").then().statusCode(404);
	}

	public boolean findPerson(final PersonId personId) {
		return em.find(PersonListEntry.class, personId.asString()) != null;
	}

	@Test
	public void testGetByIdOK() {

		// PREPARE
		final PersonId personId = new PersonId(UUID.randomUUID());
		final PersonName personName = new PersonName("Peter Parker " + personId);
		final SimpleStreamId personStreamId = new SimpleStreamId(PersonId.TYPE + "-" + personId);
		final PersonCreatedEvent event = new PersonCreatedEvent(personId, personName);
		final CommonEvent ce = new SimpleCommonEvent(new EventId(event.getEventId().asBaseType()),
				new TypeName(event.getEventType().asBaseType()), event);
		eventStore.appendToStream(personStreamId, ce);

		await().atMost(5, SECONDS).until(() -> findPerson(personId));

		// TEST & VERIFY

		final PersonListEntry person = given().pathParam("id", personId.asString()).when().get("/persons/{id}").then()
				.statusCode(200).extract().as(PersonListEntry.class);
		assertThat(person.getId(), is(equalTo(personId)));
		assertThat(person.getName(), is(equalTo(personName)));

		final PersonListEntry[] persons = given().when().get("/persons").then().statusCode(200).extract()
				.as(PersonListEntry[].class);

		assertThat(Arrays.asList(persons), is(not(empty())));
		final PersonListEntry person0 = persons[0];
		assertThat(person0.getId(), is(equalTo(personId)));
		assertThat(person0.getName(), is(equalTo(personName)));

	}

}
