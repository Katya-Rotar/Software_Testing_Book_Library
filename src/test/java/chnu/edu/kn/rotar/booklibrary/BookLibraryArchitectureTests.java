package chnu.edu.kn.rotar.booklibrary;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.*;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
/*
  @author   katya
  @project   BookLibrary
  @class  BookLibraryArchitectureTests
  @version  1.0.0 
  @since 25.10.2025 - 19.02
*/
@SpringBootTest
class BookLibraryArchitectureTests {
    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("chnu.edu.kn.rotar.booklibrary");
    }

    // 1. Layered architecture
    @Test
    void shouldFollowLayerArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(applicationClasses);
    }

    // 2. Controllers should not depend on other controllers
    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    // 3. Repositories should not depend on services
    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .check(applicationClasses);
    }

    // 4. Controller classes should be named XController
    @Test
    void controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    // 5. Controller classes should be annotated with @RestController
    @Test
    void controllerClassesShouldBeAnnotatedByControllerClass() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(RestController.class)
                .check(applicationClasses);
    }

    // 6. Repository should be interface
    @Test
    void repositoryShouldBeInterface() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .beInterfaces()
                .check(applicationClasses);
    }

    // 7. Controller fields should not be annotated with @Autowired
    @Test
    void anyControllerFieldsShouldNotBeAnnotatedAutowired() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(Autowired.class)
                .check(applicationClasses);
    }

    // 8. Model fields should be private
    @Test
    void modelFieldsShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..model..")
                .should().bePrivate()
                .check(applicationClasses);
    }

    // 9. Service classes should be annotated with @Service
    @Test
    void serviceClassesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should()
                .beAnnotatedWith(Service.class)
                .check(applicationClasses);
    }

    // 10. No circular dependencies between packages
    @Test
    void packagesShouldNotHaveCycles() {
        slices().matching("..(*)..")
                .should().beFreeOfCycles()
                .check(applicationClasses);
    }

    // 11. Service classes should not depend on controller classes
    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    // 12. Repository classes should only depend on Java/Spring or model classes
    @Test
    void repositoriesShouldDependOnlyOnModelOrFramework() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat()
                .resideOutsideOfPackages("java..", "org.springframework..", "..model..", "..repository..")
                .check(applicationClasses);
    }

    // 13. Controller methods should be public
    @Test
    void controllerMethodsShouldBePublic() {
        methods()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..controller..")
                .should().bePublic()
                .check(applicationClasses);
    }

    // 14. Service methods should be public
    @Test
    void serviceMethodsShouldBePublic() {
        methods()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..service..")
                .should().bePublic()
                .check(applicationClasses);
    }

    // 15. Model classes should not depend on controller or service
    @Test
    void modelsShouldNotDependOnControllerOrService() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..controller..", "..service..")
                .check(applicationClasses);
    }

    // 16. Controller methods should have a mapping annotation
    @Test
    void controllerMethodsShouldHaveMappingAnnotation() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RequestMapping.class)
                .orShould().beAnnotatedWith(GetMapping.class)
                .orShould().beAnnotatedWith(PostMapping.class)
                .orShould().beAnnotatedWith(PutMapping.class)
                .orShould().beAnnotatedWith(DeleteMapping.class)
                .check(applicationClasses);
    }

    // 17. No public fields in controller, service, repository
    @Test
    void noPublicFieldsInLayers() {
        fields()
                .that().areDeclaredInClassesThat()
                .resideInAnyPackage("..controller..", "..service..", "..repository..")
                .should().notBePublic()
                .check(applicationClasses);
    }

    // 18. Service classes should have names ending with "Service"
    @Test
    void serviceClassesShouldBeNamedXService() {
        classes()
                .that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .check(applicationClasses);
    }

    // 19. Repository classes should have names ending with "Repository"
    @Test
    void repositoryClassesShouldBeNamedXRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .check(applicationClasses);
    }

    // 20. Repository interfaces should extend MongoRepository
    @Test
    void repositoryInterfacesShouldExtendMongoRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .and().areInterfaces()
                .should().beAssignableTo(MongoRepository.class)
                .check(applicationClasses);
    }
}
