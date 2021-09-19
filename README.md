# ddd-cqrs-4-java-example
Example Java DDD/CQRS/Event Sourcing microservices with [Quarkus](https://quarkus.io/), [Spring Boot](https://spring.io/projects/spring-boot/) and the [EventStore](https://eventstore.org/) from Greg Young. The code uses the lightweight [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libaries. No special framework is used except the well known JEE/Spring standards.

[![Java Development Kit 11](https://img.shields.io/badge/JDK-11-green.svg)](https://openjdk.java.net/projects/jdk/11/)
[![Build Status](https://github.com/fuinorg/ddd-cqrs-4-java-example/actions/workflows/maven.yml/badge.svg)](https://github.com/fuinorg/ddd-cqrs-4-java-example/actions/workflows/maven.yml)

## Background
This application shows how to implement [DDD](https://en.wikipedia.org/wiki/Domain-driven_design), [CQRS](https://en.wikipedia.org/wiki/Command%E2%80%93query_separation) and [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) without a DDD/CQRS framework. It uses just a few small libraries in addition to  standard web application frameworks like [Quarkus](https://quarkus.io/) and [Spring Boot](https://spring.io/projects/spring-boot/).

If you are new to the DDD/CQRS topic, you can use these mindmaps to find out more: 
- [DDD Mindmap](https://www.mindmeister.com/de/177813182/ddd)
- [CQRS Mindmap](https://www.mindmeister.com/de/177815383/cqrs)

Here is an overview of how such an application looks like: 

[![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/doc/cqrs-overview-small.png)](doc/cqrs-overview.png)

## Components
- **[Shared](shared)** - Common code for all demo applications (commands, events, value objects and utilities).
- **[Aggregates](aggregates)** - DDD related code for all demo applications (aggregates, entities and business exceptions).
- **[Quarkus](quarkus)** - Two microservices (Command & Query) based on [Quarkus](https://quarkus.io/) that is the [successor of Wildfly Swarm/Thorntail](https://thorntail.io/posts/thorntail-community-announcement-on-quarkus/) and has CDI, JAX-RS and [SmallRye](https://smallrye.io/) ([Eclipse MicroProfile](http://microprofile.io/)).
- **[Spring Boot](spring-boot)** - Two microservices (Command & Query) based on [Spring Boot](https://spring.io/projects/spring-boot/).
- **[Java SE + CDI](java-se-cdi)** - Two standalone applications (Command & Query) using CDI for dependency injection.

## Getting started

### Prerequisites
Make sure you have the following tools installed/configured:
* [git](https://git-scm.com/) (VCS)
* [Docker CE](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/)
* [Docker Compose](https://docs.docker.com/compose/)
* *OPTIONAL* [GraalVM](https://www.graalvm.org/)
* Hostname should be set in /etc/hosts (See [Find and Change Your Hostname in Ubuntu](https://helpdeskgeek.com/linux-tips/find-and-change-your-hostname-in-ubuntu/) for more information)

### Clone and install project 
1. Clone the git repository
   ```
   git clone https://github.com/fuinorg/ddd-cqrs-4-java-example.git
   ```
2. Change into the project's directory and run a Maven build
   ```
   cd ddd-cqrs-4-java-example
   ./mvnw install
   ```
   Be patient - This may take a while (~5 minutes) as all dependencies and some Docker images must be downloaded and also some integration tests will be executed.
   
### Start Event Store and Maria DB (Console window 1)
Change into the project's directory and run Docker Compose
```
cd ddd-cqrs-4-java-example
docker-compose up
```

### Start command / query implementations
Start one query service and then one command service.
You can mix Quarkus & Spring Boot if you want to!

#### Quarkus Microservices

##### Quarkus Query Service (Console window 2)
1. Start the Quarkus query service:
   ```
   cd ddd-cqrs-4-java-example/quarkus/query
   ./mvnw quarkus:dev
   ```
2. Opening [http://localhost:8080/](http://localhost:8080/) should show the query welcome page

For more details see [quarkus/query](quarkus/query).

##### Quarkus Command Service (Console window 3)
1. Start the Quarkus command service:   
   ```
   cd ddd-cqrs-4-java-example/quarkus/command
   ./mvnw quarkus:dev
   ```
2. Opening [http://localhost:8081/](http://localhost:8081/) should show the command welcome page

For more details see [quarkus/command](quarkus/command).

#### Spring Boot Microservices

##### Spring Boot Query Service (Console window 2)
1. Start the Spring Boot query service:   
   ```
   cd ddd-cqrs-4-java-example/spring-boot/query
   ./mvnw spring-boot:run
   ```
2. Opening [http://localhost:8080/](http://localhost:8080/) should show the query welcome page

For more details see [spring-boot/query](spring-boot/query).

##### Spring Boot Command Service (Console window 3)
1. Start the Spring Boot command service:   
   ```
   cd ddd-cqrs-4-java-example/spring-boot/command
   ./mvnw spring-boot:run
   ```
2. Opening [http://localhost:8081/](http://localhost:8081/) should show the command welcome page

For more details see [spring-boot/command](spring-boot/command).

### Verify projection and query data
1. Open [http://localhost:2113/](http://localhost:2113/) to access the event store UI (User: admin / Password: changeit)
   You should see a projection named "qry-person-stream" when you click on "Projections" in the top menu.
2. Opening [http://localhost:8080/persons](http://localhost:8080/persons) should show an empty JSON array

### Execute a test command (Console window 4)
Change into the demo directory and execute a command using cURL (See [shell script](demo/create-person-command.sh) and [command](demo/create-person-command.json)) 
```
cd ddd-cqrs-4-java-example/demo
./create-person-command.sh
```   
Command service (Console window 3) should show something like
```
Update aggregate: id=PERSON 84565d62-115e-4502-b7c9-38ad69c64b05, version=-1, nextVersion=0
```   
Query service (Console window 2) should show something like
```
PersonCreatedEventHandler ... Handle PersonCreatedEvent: Person 'Peter Parker' was created
```    

### Verify the query data was updated
1. Refreshing [http://localhost:8080/persons](http://localhost:8080/persons) should show
    ```json
    [{"id":"84565d62-115e-4502-b7c9-38ad69c64b05","name":"Peter Parker"}]
    ```
2. Opening [http://localhost:8080/persons/84565d62-115e-4502-b7c9-38ad69c64b05](http://localhost:8080/persons/84565d62-115e-4502-b7c9-38ad69c64b05) should show
    ```json
    {"id":"84565d62-115e-4502-b7c9-38ad69c64b05","name":"Peter Parker"}
3. The event sourced data of the person aggregate could be found in a stream named [PERSON-84565d62-115e-4502-b7c9-38ad69c64b05](http://localhost:2113/web/index.html#/streams/PERSON-84565d62-115e-4502-b7c9-38ad69c64b05)


### Stop Event Store and Maria DB and clean up
1. Stop Docker Compose (Ubuntu shortcut = ctrl c)
2. Remove Docker Compose container
   ```   
   docker-compose rm
   ```
