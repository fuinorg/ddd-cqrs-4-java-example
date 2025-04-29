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
