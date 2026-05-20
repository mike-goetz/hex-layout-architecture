plugins {
    id("org.springframework.boot")
}

dependencies {
    // 1. Abhängigkeit nach innen
    implementation(project(":travel-bff:business"))

    // 2. Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 3. REST-Clients (um mit travel-core zu kommunizieren)
    // Wir nutzen hier WebClient aus WebFlux oder RestTemplate
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // 4. (Optional) Security für Session-Cookies (OAuth2 / OIDC)
    // implementation("org.springframework.boot:spring-boot-starter-security")
    // implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // 5. MapStruct für DTO <-> Domain Mapping im BFF
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    archiveClassifier.set("bff")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}