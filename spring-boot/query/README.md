# cqrs4j-spring-example-query
Query microservice that uses [Spring Boot](https://spring.io/projects/spring-boot/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. Events are stored in an [EventStore](https://eventstore.org/) and the query data is retrieved from a [MariaDB](https://mariadb.org/) database.

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

1. Start the Eventstore Docker container:

```
docker run -d --name eventstore-node \
-p 2113:2113 \
-p 1113:1113 \
--rm \
eventstore/eventstore:release-5.0.9
```

2. Start the MariaDB Docker container:

```
docker run -d --name mariadb \
-p 3306:3306 \
-e MYSQL_INITDB_SKIP_TZINFO=1 \
-e MYSQL_ROOT_PASSWORD=xyz \
-e MYSQL_DATABASE=querydb \
-e MYSQL_USER=mary \
-e MYSQL_PASSWORD=abc \
--rm \
mariadb:10.4
```

3. Run the test: [PersonControllerIT.java](src/test/java/org/fuin/cqrs4j/example/spring/query/api/PersonControllerIT.java)

4. Run `docker ps` to see the CONTAINER IDs and stop the Eventstore and MariaDB with `docker stop <CONTAINER_ID>`

# TODO ...

**Issues**
- [IOException / Stream closed with JSON-B](https://github.com/fuinorg/ddd-cqrs-4-java-example/issues/4)
