# cqrs4j-quarkus-example-command
Command microservice that uses [Quarkus](https://quarkus.io/), [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries. Events are stored in an [EventStore](https://eventstore.org/).

## Prerequisites
Make sure you installed everything as described [here](../../../../).

## Run the command microservice in development mode
1. Open a console (Ubuntu shortcut = ctrl alt t)
2. Start the command microservice:   
   ```
   cd ddd-cqrs-4-java-example/quarkus/command
   ./mvnw quarkus:dev
   ```
3. Opening [http://localhost:8081/](http://localhost:8081/) should show the command welcome page
   
## Overview
![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/quarkus/command/doc/cdi-command.png)


# TODO ... (Does currently not work)

## *OPTIONAL* Build and run the command microservice in native mode
1. Make sure you have enough memory (~6-8 GB) on your PC or VM
2. Open a console (Ubuntu shortcut = ctrl alt t)
3. Build the native executable 
   ```
   cd command
   ./mvnw verify -Pnative
   ```
4. Run the microservice
   ```
    ./target/cqrs4j-quarkus-example-command-1.0-SNAPSHOT-runner \
        -Djava.library.path=$GRAALVM_HOME/jre/lib/amd64 \
        -Djavax.net.ssl.trustStore=$GRAALVM_HOME/jre/lib/security/cacerts
   ```

**Issues**
- [Quarkus native command microservice fails with Yasson NullPointerException](https://github.com/fuinorg/ddd-cqrs-4-java-example/issues/2)
