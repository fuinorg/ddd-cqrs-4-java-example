package org.fuin.cqrs4j.example.spring.shared;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import org.fuin.ddd4j.core.AggregateRootUuid;
import org.fuin.ddd4j.core.DomainEvent;
import org.fuin.ddd4j.core.EntityId;
import org.fuin.ddd4j.core.HasEntityTypeConstant;
import org.fuin.esc.api.HasSerializedDataTypeConstant;
import org.fuin.objects4j.common.HasPublicStaticValueOfMethod;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Tests architectural aspects.
 */
@AnalyzeClasses(packagesOf = ArchitectureTest.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @Test
    public void testDomainEventsAnnotations() {

        final JavaClasses importedClasses = new ClassFileImporter().importPackages(ArchitectureTest.class.getPackageName());

        classes()
                .that().implement(DomainEvent.class)
                .and().doNotHaveModifier(JavaModifier.ABSTRACT)
                .should().beAnnotatedWith(HasSerializedDataTypeConstant.class)
                .check(importedClasses);
    }
    @Test
    void testEntityIdAnnotations() {

        final JavaClasses importedClasses = new ClassFileImporter().importPackages(ArchitectureTest.class.getPackageName());

        classes()
                .that().areAssignableTo(AggregateRootUuid.class)
                .and().implement(EntityId.class)
                .should().beAnnotatedWith(HasPublicStaticValueOfMethod.class)
                .andShould().beAnnotatedWith(HasEntityTypeConstant.class)
                .allowEmptyShould(true).check(importedClasses);

    }

}
