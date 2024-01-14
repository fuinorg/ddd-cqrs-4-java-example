package org.fuin.cqrs4j.example.shared;

import org.fuin.ddd4j.codegen.AggregateRootUuidVO;

@AggregateRootUuidVO(
        pkg = "org.fuin.cqrs4j.example.shared",
        name = "CustomerId",
        entityType = "CUSTOMER",
        description = "Unique identifier of a customer",
        jsonb = true
)
public interface ICustomerId {
}
