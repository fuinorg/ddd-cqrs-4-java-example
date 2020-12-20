# ddd-cqrs-4-java-example
Minimal standalone example application that uses the [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libraries and an [EventStore](https://eventstore.org/) to store the events (Event Sourcing). The query data is stored in-memory using a [HSQLDB](http://hsqldb.org/).

## Starting the demo

1. Start an EventStore locally on your PC
    * You can use the [EventStore Docker Image](https://hub.docker.com/r/eventstore/eventstore/): ```docker run --name eventstore-node -p 2113:2113 -p 1113:1113 --rm eventstore/eventstore:release-5.0.9```
    * Or simply start an instance (See [Install and run Event Store](https://eventstore.org/docs/server/index.html?tabs=tabid-1) for more information)
2. Run the [QryExampleApp](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/app/QryExampleApp.java) to make the view/read part listen to new events
     * You should see something like this on the console: ```INFO  o.f.d.qry.handler.QryProjector - Create projection 'qry-person-stream' with events: [PersonCreatedEvent]```
     * If you open the event store UI (http://localhost:2113/web/index.html#/projections) you should see a projection name 'qry-person-stream'
3. Run the [CmdExampleApp](src/main/java/org/fuin/cqrs4j/example/javasecdi/cmd/app/CmdExampleApp.java) to create a [PersonCreatedEvent](src/main/java/org/fuin/cqrs4j/example/javasecdi/shared/domain/PersonCreatedEvent.java)
      * You should see something like this on the console: ```INFO  o.f.d.cmd.app.CmdExampleApp - Updated event store...```
      * There should also be an update on the 'QryExampleApp' console: ```INFO  o.f.d.q.h.PersonCreatedEventHandler - Handle Person 'Peter Parker Inc.' was created``` 
      * If you open the [Event Store UI](http://localhost:2113/web/index.html#/projections) (User 'admin' / Password 'changeit') and open the projection details for 'qry-person-stream' it should show 'Events processed = 1'
      * Look at the person's aggregate event stream: ```http://localhost:2113/web/index.html#/streams/PERSON-00000000-0000-0000-0000-000000000000`` - Replace the zero UUID with the one shown in the event handler message 'Person 'Peter Parker Inc.' (xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx) was created'
4. Kill the [QryExampleApp](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/app/QryExampleApp.java)
5. Stop the event store

## What is in the application?      
The application is splitted into three packages: 'shared', 'cmd' and 'qry'.

### shared
Has code shared between both modules 'cmd' and 'qry'.

#### Sub package 'app'
There are several configuration classes used for CDI in the [app](src/main/java/org/fuin/cqrs4j/example/javasecdi/shared/app) package 

### cmd
Code for the 'command' or 'write' side of the application.

#### Sub package 'app'
Contains mainly the command line application.
* The command line application [CmdExampleApp](src/main/java/org/fuin/cqrs4j/example/javasecdi/cmd/app/CmdExampleApp.java) that creates a new [Person](src/main/java/org/fuin/cqrs4j/example/javasecdi/cmd/domain/Person.java) aggregate and saves the state as a series of events in the [EventStore](https://eventstore.org/) (Event Sourcing).

#### Sub package 'domain'
There are three DDD domain model related classes
* The [Person](src/main/java/org/fuin/cqrs4j/example/javasecdi/cmd/domain/Person.java) aggregate (Aggregate Root).
* The [PersonRepository](src/main/java/org/fuin/cqrs4j/example/javasecdi/cmd/domain/PersonRepository.java) used to store the aggregate root in the event store.

### Package 'qry'
Code for the 'query' or 'read' side of the application.

#### Overview
![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/java-se-cdi/doc/ddd-cqrs-4-java-example.png)

#### Sub package 'app'
There are several configuration classes used for CDI in the [app](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/app) package and the main query application.

* The command line application [QryExampleApp](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/app/QryExampleApp.java) that reads events from the [EventStore](https://eventstore.org/) (Event Sourcing) and updates it's internal view model.
* The [QryTimer](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/app/QryTimer.java) class fires an asynchronous CDI event called [QryCheckForViewUpdatesEvent](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/app/QryCheckForViewUpdatesEvent.java)


#### Sub package 'domain'
The view data will be stored in an in-memory HSQL database using JPA
* The [QryPerson](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/domain/QryPerson.java) class is an [@javax.persistence.Entity](https://javaee.github.io/javaee-spec/javadocs/javax/persistence/Entity.html) class
* The [QryPersonRepository](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/domain/QryPersonRepository.java) class is used for the [Apache DeltaSpike Data Module](https://deltaspike.apache.org/documentation/data.html) entity repository to  simplifying the database access. (See [EntityRepository](https://deltaspike.apache.org/javadoc/1.7.2/index.html?org/apache/deltaspike/data/api/EntityRepository.html)) 

#### Sub package 'handler'
The query side has to listen for new events. This is done by polling the Event Store every second.

* The [QryProjector](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/handler/QryProjector.java) receives the event issued by the [QryTimer](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/app/QryTimer.java) and starts reading a from a projection that collects all necessary events required for one view.
* The [QryEventChunkHandler](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/handler/QryEventChunkHandler.java) is used to read all events of a single projection and dispatch them to the event handler that want to have them. It also updates the [QryPersonProjectionPosition](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/handler/QryPersonProjectionPosition.java) in the database. This is done to remember, what the last event was that was read from the event store's event stream (projection). It uses a helper class called [SimpleEventDispatcher](https://github.com/fuinorg/cqrs-4-java/blob/master/src/main/java/org/fuin/cqrs4j/SimpleEventDispatcher.java) that is aware of all known events and capable to handle the 'dispatch' operation.
* The [PersonCreatedEventHandler](src/main/java/org/fuin/cqrs4j/example/javasecdi/qry/handler/PersonCreatedEventHandler.java) class is an example of an event handler that updates the local view database using a single event.
 
