# cqrs4j-spring-example-command
Command microservice that uses [Spring Boot](https://spring.io/projects/spring-boot/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. Events are stored in an [EventStore](https://eventstore.org/).

## Prerequisites
Make sure you installed everything as described [here](../../../../).

## Run the command microservice in development mode
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Start the command microservice:   
   ```
   cd ddd-cqrs-4-java-example/spring-boot/command
   ./mvnw spring-boot:run
   ```
3. Opening [http://localhost:8081/](http://localhost:8081/) should show the command welcome page
   
## Overview
![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/spring-boot/command/doc/spring-command.png)


# TODO ...

**Issues**
- [IOException / Stream closed with JSON-B](https://github.com/fuinorg/ddd-cqrs-4-java-example/issues/4)
