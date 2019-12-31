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

# TODO ...

**Issues**
- [IOException / Stream closed with JSON-B](https://github.com/fuinorg/ddd-cqrs-4-java-example/issues/4)
