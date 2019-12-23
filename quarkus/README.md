# cqrs4j-quarkus-example
Example applications that uses [Quarkus](https://quarkus.io/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. Events are stored in an [EventStore](https://eventstore.org/) and the query data is retrieved from a [PostgreSQL](https://www.postgresql.org/) database.

## Prerequisites
Make sure you have the following tools installed:
* [git](https://git-scm.com/) (VCS)
* [Docker CE](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/)
* [Docker Compose](https://docs.docker.com/compose/)
* [GraalVM](https://www.graalvm.org/)

Or simply use the [lubuntu-developer-vm](https://github.com/fuinorg/lubuntu-developer-vm) that has already everything installed.
Then execute the following steps:
1. Open a console (Ubuntu shortcut = <ctrl><alt><t>)
2. Install GraalVM: 
   ```
   sdk install java 19.2.1-grl
   ```
3. Finalize GraalVM settings    
   ```
   echo "GRAALVM_HOME=\"/home/developer/.sdkman/candidates/java/19.2.1-grl\"" >> ~/.profile
   source source ~/.profile
   $GRAALVM_HOME/bin/gu install native-image
   ``` 

## Getting started
1. Open a console (Ubuntu shortcut = <ctrl><alt><t>)
2. Clone the git repository
   ```
   git clone https://github.com/fuinorg/ddd-cqrs-4-java-example.git
   ```
3. Change into the new directory 
   ```
   cd ddd-cqrs-4-java-example/quarkus
   ```
4. Start Event Store and PostgreSQL using Docker Compose
   ```
   docker-compose up
   ```
   (Wait until everything is started) 
5. Open another console (Ubuntu shortcut = <ctrl><alt><t>)
6. Change into the quarkus directory and build the project
   ```
   cd ddd-cqrs-4-java-example/quarkus
   ./mvnw install
   ```

## Run the query microservice in development mode
1. Start the query microservice:   
   ```
   cd query
   ./mvnw quarkus:dev
   ```
2. Opening [http://localhost:8080/persons](http://localhost:8080/persons) should show an empty JSON array
3. Open another console (Ubuntu shortcut = <ctrl><alt><t>)
4. Change into the demo directory and add an event using cURL 
   ```
   cd ddd-cqrs-4-java-example/quarkus/query/demo
   ./add-person-created-event.sh
   ```
5. Refreshing [http://localhost:8080/persons](http://localhost:8080/persons) should show something like this:
    ```json
    [{"id":"f645969a-402d-41a9-882b-d2d8000d0f43","name":"Peter Parker"}]
    ```
