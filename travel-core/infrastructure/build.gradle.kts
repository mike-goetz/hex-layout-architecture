plugins {
    // Nur hier wird das ausführbare Fat-JAR gebaut
    id("org.springframework.boot")
}

dependencies {
    // 1. Abhängigkeit nach innen: Infrastructure MUSS Business kennen
    implementation(project(":travel-core:business"))
    implementation(project(":travel-core:domain"))

    // 2. Spring Boot Web, JPA & Validation
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // 3. PostgreSQL-Treiber für die Runtime
    runtimeOnly("org.postgresql:postgresql")

    // 4. Flyway Core & das spezifische PostgreSQL-Modul (Zwingend erforderlich für Spring Boot 4)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // 5. OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // 6. MapStruct für DTO <-> Domain Mapping
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // 7. Other things
    implementation("commons-io:commons-io:2.22.0")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:7.3.4.Final")

    // 8. Test Setup
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 9. Testcontainers Setup (Spring Boot 4 Integration)
    // Ermöglicht die automatische Bereitstellung von PostgreSQL via @ServiceConnection im Test
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    archiveClassifier.set("core")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}