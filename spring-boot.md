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
