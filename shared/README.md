# cqrs4j-example-shared
Shared code for all demo applications and client &amp; server.

> :information_source: There is often a discussion if exposing a library like this one to the outside world is a good idea. In case of a homogenous environment it can be OK to share commands, events and value objects as a library. This will save some time as they are used all over the place. If you don't like this approach, simply copy the content of this library into your microservice projects.

## Commands
- [CreatePersonCommand](src/main/java/org/fuin/cqrs4j/example/shared/CreatePersonCommand.java) - A new person should be created in the system. (Example: [CreatePersonCommand.json](src/test/resources/commands/CreatePersonCommand.json))

## Events
- [PersonCreatedEvent](src/main/java/org/fuin/cqrs4j/example/shared/PersonCreatedEvent.java) - A new person was created in the system. (Example: [PersonCreatedEvent.json](src/test/resources/events/PersonCreatedEvent.json))

## Value Objects
- [PersonId](src/main/java/org/fuin/cqrs4j/example/shared/PersonId.java) - Identifies uniquely a person aggregate.
- [PersonName](src/main/java/org/fuin/cqrs4j/example/shared/PersonName.java) - Name of a person.

## Supporting classes
- [SharedEntityIdFactory](src/main/java/org/fuin/cqrs4j/example/shared/SharedEntityIdFactory.java) - Factory that creates entity identifier instances based on the type.
- [SharedUtils](src/main/java/org/fuin/cqrs4j/example/shared/SharedUtils.java) - Utilities.

