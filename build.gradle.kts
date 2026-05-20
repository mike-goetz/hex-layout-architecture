plugins {
    id("org.springframework.boot") version "4.0.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.example.travel"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Java-Konfiguration AUSSCHLIESSLICH für Backend-Module (ohne travel-ui)
configure(subprojects.filter { it.name != "travel-ui" }) {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(26))
        }
    }

    // Explizite Typisierung der Dependency Management Extension zur Behebung des Kompilierungsfehlers
    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.6")
        }
    }

    dependencies {
        // Lombok für alle Java-Layer (Aufruf über String-Literale, da Accessors im configure-Block fehlen)
        "compileOnly"("org.projectlombok:lombok")
        "annotationProcessor"("org.projectlombok:lombok")
        "testCompileOnly"("org.projectlombok:lombok")
        "testAnnotationProcessor"("org.projectlombok:lombok")

        // Testing-Basics (Versionen werden über das Spring Boot BOM verwaltet)
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}