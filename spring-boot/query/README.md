# cqrs4j-spring-example-query
Query microservice that uses [Spring Boot](https://spring.io/projects/spring-boot/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. 
Events are stored in an [EventStore](https://eventstore.org/) and the query data is retrieved from a [MariaDB](https://mariadb.org/) database.

## CQRS Views
* [personlist](src/main/java/org/fuin/cqrs4j/example/spring/query/views/personlist)
  * [PersonListView](src/main/java/org/fuin/cqrs4j/example/spring/query/views/personlist/PersonListView.java) - Defines a view with a list of persons. It maps incoming events to the database entities.
  * [PersonListEntry](src/main/java/org/fuin/cqrs4j/example/spring/query/views/personlist/PersonListEntry.java) - JPA Entity to store a person in the database
  * [PersonListController](src/main/java/org/fuin/cqrs4j/example/spring/query/views/personlist/PersonListController.java) - Spring Rest Controller to return the persons
* [statistics](src/main/java/org/fuin/cqrs4j/example/spring/query/views/statistics)

## Prerequisites
Make sure you installed everything as described [here](../../../../).

## Run the query microservice in development mode
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Start the query microservice:   
   ```
   cd ddd-cqrs-4-java-example/spring-boot/query
   ./mvnw spring-boot:run
   ```
3. Opening [http://localhost:8080/](http://localhost:8080/) should show the query welcome page

## Overview
![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/spring-boot/query/doc/spring-view.png)

## Running test in IDE
In case you want to run the integration test inside your IDE (Eclipse or other), you need to start the Eventstore and MariaDB before.

1. Start the Eventstore and MariaDB Docker container using the [docker-compose.yml](../../docker-compose.yml) script: `docker-compose up`
2. Run the test: [PersonControllerIT.java](src/test/java/org/fuin/cqrs4j/example/spring/query/api/PersonControllerIT.java)
3. Stop the containers in the console using CTRL+C and then remove the containers using again Docker Compose: `docker-compose rm`
