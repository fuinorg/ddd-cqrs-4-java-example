# cqrs4j-spring-example-command
Command microservice that uses [Spring Boot](https://spring.io/projects/spring-boot/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. 
Events are stored in an [EventStore](https://eventstore.org/).

## Domain classes
* Aggregates (DDD)
  * [Person](src/main/java/org/fuin/cqrs4j/example/spring/command/domain/Person.java) 
* Commands (CQRS)
  * [CreatePersonCommand](src/main/java/org/fuin/cqrs4j/example/spring/command/domain/CreatePersonCommand.java)
  * [DeletePersonCommand](src/main/java/org/fuin/cqrs4j/example/spring/command/domain/DeletePersonCommand.java) 
* Repository (DDD)
  * [PersonRepository](src/main/java/org/fuin/cqrs4j/example/spring/command/domain/PersonRepository.java)
  * [EventStorePersonRepository](src/main/java/org/fuin/cqrs4j/example/spring/command/domain/EventStorePersonRepository.java) (Eventstore implementation)

## Prerequisites
Make sure you installed everything as described [here](../../README.md).

## Run the command microservice in development mode
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Start the command microservice:   
   ```
   cd ddd-cqrs-4-java-example/spring-boot/command
   ./mvnw spring-boot:run
   ```
3. Opening http://localhost:8081/ should show the command welcome page
   
## Overview
![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/spring-boot/command/doc/spring-command.png)


