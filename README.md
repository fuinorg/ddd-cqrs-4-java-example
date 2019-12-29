# ddd-cqrs-4-java-example
Example applications that use [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries and an [EventStore](https://eventstore.org/) to store the events (Event Sourcing).

## Components
- **Shared** - Common code for all demo applications (commands, events, value objects and utilities). [More...](shared)
- **Aggregates** - DDD related code for all demo applications (aggregates, entities and business exceptions). [More...](aggregates)
- **Java SE + CDI** - Two standalone applications (Command & Query) using CDI for dependency injection. [More...](java-se-cdi)
- **Quarkus** - Two web applications (Command & Query) based on [Quarkus](https://quarkus.io/). [More...](quarkus)
- **Spring Boot** - Two web applications (Command & Query) based on [Spring Boot](https://spring.io/projects/spring-boot/). [More...](spring-boot)

## Getting started

### Prerequisites
Make sure you have the following tools installed/configured:
* [git](https://git-scm.com/) (VCS)
* [Docker CE](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/)
* [Docker Compose](https://docs.docker.com/compose/)
* *OPTIONAL* [GraalVM](https://www.graalvm.org/)
* Hostname should be set in /etc/hosts (See [Find and Change Your Hostname in Ubuntu](https://helpdeskgeek.com/linux-tips/find-and-change-your-hostname-in-ubuntu/) for more information)

Or simply use the [lubuntu-developer-vm](https://github.com/fuinorg/lubuntu-developer-vm) that has already everything installed. Then execute the following steps:
1. Download and install the developer VM as described
2. OPTIONAL: Change memory of VM to 6 GB (instead of 4 GB default) if you want to create a native image with GraalVM 
3. Start the VM and login (developer / developer)
4. Open a console (Shortcut = ctrl alt t)
5. Run a small script that finalizes the setup of the developer virtual machine
   ```
   bash <(curl -s https://raw.githubusercontent.com/fuinorg/ddd-cqrs-4-java-example/master/setup-lubuntu-developer-vm.sh)
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
   
### Start Event Store and Maria DB
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Change into the project's directory and run Docker Compose
   ```
   cd ddd-cqrs-4-java-example
   docker-compose up
   ```

### Start command / query implementations
Start one command and one query microservice. 

- Quarkus Microservices
  - [Command](quarkus/command)
  - [Query](quarkus/query)
  
- Spring Boot Microservices
  - [Command](spring-boot/command)
  - [Query](spring-boot/query)

### Test
1. Open [http://localhost:2113/](http://localhost:2113/) to access the event store UI (User: admin / Password: changeit)
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

### Stop Event Store and Maria DB and clean up
1. Stop Docker Compose (Ubuntu shortcut = ctrl c)
2. Remove Docker Compose container
   ```   
   docker-compose rm
   ```
