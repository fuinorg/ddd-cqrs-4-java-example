package org.fuin.cqrs4j.example.shared;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.fuin.cqrs4j.Command;
import org.fuin.ddd4j.ddd.*;
import org.fuin.objects4j.common.HasPublicStaticIsValidMethod;
import org.fuin.objects4j.common.HasPublicStaticValueOfMethod;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Tests architectural aspects.
 */
public class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void beforeAll() {
        importedClasses = new ClassFileImporter().importPackages("org.fuin.cqrs4j.example.shared");
    }

    @Test
    public void testDomainEventsAnnotations() {

        classes()
                .that().implement(DomainEvent.class).and().doNotImplement(Command.class)
                .should().beAnnotatedWith(SerializedDataTypeConstant.class)
                .check(importedClasses);
    }

    @Test
    public void testEntityIdAnnotations() {

        classes()
                .that().areAssignableTo(AggregateRootUuid.class)
                .and().implement(EntityId.class)
                .should().beAnnotatedWith(HasPublicStaticValueOfMethod.class)
                .andShould().beAnnotatedWith(HasEntityTypeConstant.class)
                .allowEmptyShould(true).check(importedClasses);

    }

}
