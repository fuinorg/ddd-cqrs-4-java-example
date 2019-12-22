package org.fuin.cqrs4j.example.quarkus.query;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PersonResourceTest {

    @Test
    public void testGetAll() {
        given()
          .when().get("/persons")
          .then()
             .statusCode(200);
    }

}