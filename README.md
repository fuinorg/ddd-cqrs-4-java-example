# ddd-cqrs-4-java-example
Example microservices that use [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries and an [EventStore](https://eventstore.org/) to store the events (Event Sourcing).

[![Java Development Kit 1.8](https://img.shields.io/badge/JDK-1.8-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

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
#### Option 1: Install everything yourself 
Make sure you have the following tools installed/configured:
* [git](https://git-scm.com/) (VCS)
* [Docker CE](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/)
* [Docker Compose](https://docs.docker.com/compose/)
* *OPTIONAL* [GraalVM](https://www.graalvm.org/)
* Hostname should be set in /etc/hosts (See [Find and Change Your Hostname in Ubuntu](https://helpdeskgeek.com/linux-tips/find-and-change-your-hostname-in-ubuntu/) for more information)

#### :star: Option 2: Use lubuntu-developer-vm :star:
The **[lubuntu-developer-vm](https://github.com/fuinorg/lubuntu-developer-vm)** has already (almost) everything installed. You only need to  execute the following steps:
1. Download and install the [lubuntu-developer-vm](https://github.com/fuinorg/lubuntu-developer-vm) as described
2. OPTIONAL: Change memory of VM to 6 GB (instead of 4 GB default) if you want to create a native image with GraalVM 
3. Start the VM and login (developer / developer)
4. Open a console (Shortcut = ctrl alt t)
5. Run a script that finalizes the setup of the developer virtual machine
   ```
   bash <(curl \
   -s https://raw.githubusercontent.com/fuinorg/ddd-cqrs-4-java-example/master/setup-lubuntu-developer-vm.sh)   
   ```
6. Reboot
   ```
   reboot
   ```

### Clone and install project 
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Clone the git repository
   ```
   git clone https://github.com/fuinorg/ddd-cqrs-4-java-example.git
   ```
3. Change into the project's directory and run a Maven build
   ```
   cd ddd-cqrs-4-java-example
   ./mvnw install
   ```
   Be patient - This may take a while (~5 minutes) as all dependencies and some Docker images must be downloaded and also some integration tests will be executed.
   
### Start Event Store and Maria DB
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Change into the project's directory and run Docker Compose
   ```
   cd ddd-cqrs-4-java-example
   docker-compose up
   ```

### Start command / query implementations
Start one command microservice and one query microservice - You can mix Quarkus & Spring Boot!  

#### Quarkus Microservices
| Module        |       |
| :------------ | :---- |
| **[Command](quarkus/command)** | [![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/quarkus/command/doc/cdi-command-small.png)](quarkus/command) |
| **[Query](quarkus/query)** | [![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/quarkus/query/doc/cdi-view-small.png)](quarkus/query) |
  
#### Spring Boot Microservices
| Module        |       |
| :------------ | :---- |
| **[Command](spring-boot/command)** | [![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/spring-boot/command/doc/spring-command-small.png)](spring-boot/command)  |
| **[Query](spring-boot/query)** | [![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/spring-boot/query/doc/spring-view-small.png)](spring-boot/query)  |


### Test
1. Open [http://localhost:2113/](http://localhost:2113/) to access the event store UI (User: admin / Password: changeit)
   You should see a projection named "qry-person-stream" when you click on "Projections" in the top menu.
2. Opening [http://localhost:8080/persons](http://localhost:8080/persons) should show an empty JSON array
3. Open a console (Ubuntu shortcut = ctrl alt t)
4. Change into the demo directory and execute a command using cURL (See [shell script](demo/create-person-command.sh) and [command](demo/create-person-command.json)) 
   ```
   cd ddd-cqrs-4-java-example/demo
   ./create-person-command.sh
   ```   
   Command console should show something like
   ```
   Update aggregate: id=PERSON 84565d62-115e-4502-b7c9-38ad69c64b05, version=-1, nextVersion=0
   ```   
   Query console should show something like
   ```
   PersonCreatedEventHandler ... Handle PersonCreatedEvent: Person 'Peter Parker' was created
   ```    
4. Refreshing [http://localhost:8080/persons](http://localhost:8080/persons) should show
    ```json
    [{"id":"84565d62-115e-4502-b7c9-38ad69c64b05","name":"Peter Parker"}]
    ```
5. Opening [http://localhost:8080/persons/84565d62-115e-4502-b7c9-38ad69c64b05](http://localhost:8080/persons/84565d62-115e-4502-b7c9-38ad69c64b05) should show
    ```json
    {"id":"84565d62-115e-4502-b7c9-38ad69c64b05","name":"Peter Parker"}
6. The event sourced data of the person aggregate could be found in a stream named [PERSON-84565d62-115e-4502-b7c9-38ad69c64b05](http://localhost:2113/web/index.html#/streams/PERSON-84565d62-115e-4502-b7c9-38ad69c64b05)


### Stop Event Store and Maria DB and clean up
1. Stop Docker Compose (Ubuntu shortcut = ctrl c)
2. Remove Docker Compose container
   ```   
   docker-compose rm
   ```
