# ddd-cqrs-4-java-example
Example Java DDD/CQRS/Event Sourcing microservices with [Quarkus](https://quarkus.io/), [Spring Boot](https://spring.io/projects/spring-boot/) and the [EventStore](https://eventstore.org/) from Greg Young. The code uses the lightweight [ddd-4-java](https://github.com/fuinorg/ddd-4-java) and [cqrs-4-java](https://github.com/fuinorg/cqrs-4-java) libaries. No special framework is used except the well known JEE/Spring standards.

[![Java Development Kit 17](https://img.shields.io/badge/JDK-17-green.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Apache](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://github.com/fuinorg/ddd-cqrs-4-java-example/actions/workflows/maven.yml/badge.svg)](https://github.com/fuinorg/ddd-cqrs-4-java-example/actions/workflows/maven.yml)

## Background
This application shows how to implement [DDD](https://en.wikipedia.org/wiki/Domain-driven_design), [CQRS](https://en.wikipedia.org/wiki/Command%E2%80%93query_separation) and [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) without a DDD/CQRS framework. It uses just a few small libraries in addition to  standard web application frameworks like [Quarkus](https://quarkus.io/) and [Spring Boot](https://spring.io/projects/spring-boot/).

If you are new to the DDD/CQRS topic, you can use these mindmaps to find out more: 
- [DDD Mindmap](https://www.mindmeister.com/de/177813182/ddd)
- [CQRS Mindmap](https://www.mindmeister.com/de/177815383/cqrs)

Here is an overview of how such an application looks like: 

[![Overview](https://raw.github.com/fuinorg/ddd-cqrs-4-java-example/master/doc/cqrs-overview-small.png)](doc/cqrs-overview.png)

## Modules
- **[Quarkus](quarkus)** - Two microservices (Command & Query) based on [Quarkus](https://quarkus.io/).
- **[Spring Boot](spring-boot)** - Two microservices (Command & Query) based on [Spring Boot](https://spring.io/projects/spring-boot/).

## Getting started
The following instructions are tested on Linux (Ubuntu 24)

**CAUTION:** Building and running on Windows will require some (small) changes.

### Prerequisites
Make sure you have the following tools installed/configured:
* [git](https://git-scm.com/) (VCS)
* [Docker CE](https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/)
* [Docker Compose](https://docs.docker.com/compose/)
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

## Run the examples
With the services above running, start **one** query service and **one** command service. You can mix
Quarkus and Spring Boot, because the two services integrate only through the event store:
- **[Spring Boot](spring-boot.md)** - command & query on Spring Boot
- **[Quarkus](quarkus.md)** - command & query on Quarkus

Then drive and verify the running system with the demos.

## Demos
Each demo is self-contained in its own folder under [`demo/`](demo) with a `README.md` and its scripts:
- **[End-to-end demo](demo/e2e/README.md)** - the full `command → event store → query` round trip, driven
  by scripts and by an automated test, including mixing the Quarkus and Spring Boot stacks.
- **[Demos roadmap](demos-roadmap.md)** - the full set of demos (the E2E demo above plus backend
  portability, crypto-shredding, rolling-deploy event versioning, projection high-availability,
  observability).

### Stop Event Store and Maria DB and clean up
1. Stop Docker Compose (Ubuntu shortcut = ctrl c)
2. Remove Docker Compose container
   ```   
   docker-compose rm
   ```
