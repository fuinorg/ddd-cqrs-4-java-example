package org.fuin.cqrs4j.example.spring.command.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.json.bind.Jsonb;

import org.fuin.cqrs4j.ResultType;
import org.fuin.cqrs4j.SimpleResult;
import org.fuin.cqrs4j.example.shared.CreatePersonCommand;
import org.fuin.cqrs4j.example.shared.PersonCreatedEvent;
import org.fuin.cqrs4j.example.shared.PersonId;
import org.fuin.cqrs4j.example.shared.PersonName;
import org.fuin.cqrs4j.example.spring.command.app.CmdApplication;
import org.fuin.esc.api.CommonEvent;
import org.fuin.esc.api.SimpleStreamId;
import org.fuin.esc.api.StreamEventsSlice;
import org.fuin.esc.api.TypeName;
import org.fuin.esc.esjc.IESJCEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = CmdApplication.class)
public class PersonControllerIT {

	@LocalServerPort
    int port;
	
	@Autowired
	WebApplicationContext wac;
	
	@Autowired
	IESJCEventStore eventStore;

	@Autowired
    Jsonb jsonb;
    
	@BeforeEach
	public void initRestAssuredMockMvcStandalone() {
		RestAssured.port = port;
		RestAssuredMockMvc.webAppContextSetup(wac);
	}
	
    @Test
    public void testCreate() {
        
        // PREPARE
        final PersonId personId = new PersonId(UUID.randomUUID());
        final PersonName personName = new PersonName("Peter Parker");
        final CreatePersonCommand cmd = new CreatePersonCommand(personId, personName);
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
            .extract()
            .as(SimpleResult.class);
        assertThat(result.getType(), is(equalTo(ResultType.OK)));
        
        final SimpleStreamId personStreamId = new SimpleStreamId(PersonId.TYPE + "-" + personId);
        final StreamEventsSlice slice = eventStore.readEventsForward(personStreamId, 0, 1);
        final List<CommonEvent> events = slice.getEvents();
        assertThat(Arrays.asList(events), is(not(empty())));
        final CommonEvent ce = events.get(0);
        assertThat(ce.getDataType(), is(equalTo(new TypeName(PersonCreatedEvent.TYPE.asBaseType()))));
        final PersonCreatedEvent event = (PersonCreatedEvent) ce.getData();
        assertThat(event.getEntityId(), is(equalTo(personId)));
        assertThat(event.getName(), is(equalTo(personName)));
        
    }

}