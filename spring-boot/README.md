# cqrs4j-spring-example
Example applications that uses [Spring Boot](https://spring.io/projects/spring-boot/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. 
Events are stored in an [EventStore](https://eventstore.org/) and the query data is retrieved from a [MariaDB](https://mariadb.org/) database.

## Prerequisites
Make sure you installed everything as described [here](../README.md).

## Start command / query implementation
Start the command and query microservice s. 
- [Command](command) - Command microservice
- [Query](query) - Query microservice
- [Shared](shared) - Code shared between command and query modules (commands, events, value objects and utilities)
