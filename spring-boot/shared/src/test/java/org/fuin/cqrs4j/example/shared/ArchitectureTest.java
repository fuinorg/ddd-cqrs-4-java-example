package org.fuin.cqrs4j.example.shared;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.fuin.cqrs4j.core.AggregateCommand;
import org.fuin.ddd4j.core.AggregateRootUuid;
import org.fuin.ddd4j.core.DomainEvent;
import org.fuin.ddd4j.core.EntityId;
import org.fuin.ddd4j.core.HasEntityTypeConstant;
import org.fuin.esc.api.HasSerializedDataTypeConstant;
import org.fuin.objects4j.common.HasPublicStaticValueOfMethod;
import org.fuin.utils4j.Utils4J;
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
    public void testX() {
        Utils4J.classpathFiles().forEach(System.out::println);
    }

    @Test
    public void testDomainEventsAnnotations() {

        classes()
                .that().implement(DomainEvent.class).and().doNotImplement(AggregateCommand.class)
                .should().beAnnotatedWith(HasSerializedDataTypeConstant.class)
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
