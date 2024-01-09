# cqrs4j-quarkus-example-query
Query microservice that uses [Quarkus](https://quarkus.io/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. Events are stored in an [EventStore](https://eventstore.org/) and the query data is retrieved from a [MariaDB](https://mariadb.org/) database.

## Prerequisites
Make sure you installed everything as described [here](../../../../).

## Run the query microservice in development mode
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Start the query microservice:   
   ```
   cd ddd-cqrs-4-java-example/quarkus/query
   ./mvnw quarkus:dev
   ```
3. Opening [http://localhost:8080/](http://localhost:8080/) should show the query welcome page

## Overview
![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/quarkus/query/doc/cdi-view.png)

## Running test in IDE
In case you want to run the integration test inside your IDE (Eclipse or other), you need to start the Eventstore and MariaDB before.

1. Start the Eventstore and MariaDB Docker container using the [docker-compose.yml](../../docker-compose.yml) script: `docker-compose up`
2. Run the test: [QryPersonResourceIT.java](src/test/java/org/fuin/cqrs4j/example/quarkus/query/api/QryPersonResourceIT.java)
3. Stop the containers in the console using CTRL+C and then remove the containers using again Docker Compose: `docker-compose rm`

# TODO ... (Does currently not work)

## *OPTIONAL* Build and run the query microservice in native mode
1. Make sure you have enough memory (~6-8 GB) on your PC or VM
2. Open a console (Ubuntu shortcut = ctrl alt t)
3. Build the native executable 
   ```
   cd query
   ./mvnw verify -Pnative
   ```
4. Run the microservice
   ```
    ./target/cqrs4j-quarkus-example-query-0.4.0-SNAPSHOT-runner \
        -Djava.library.path=$GRAALVM_HOME/jre/lib/amd64 \
        -Djavax.net.ssl.trustStore=$GRAALVM_HOME/jre/lib/security/cacerts
   ```

**Issues**
- [Quarkus native query microservice does not execute updates](https://github.com/fuinorg/ddd-cqrs-4-java-example/issues/1)
- [Building native query microservice fails with PostgreSQL (MariaDB works fine)](https://github.com/fuinorg/ddd-cqrs-4-java-example/issues/3)
