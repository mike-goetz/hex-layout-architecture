dependencies {
    // 1. Abhängigkeit nach innen: Business MUSS Domain kennen
    implementation(project(":travel-core:domain"))

    // 2. Spring Basics (KEIN spring-boot-starter-web oder jpa!)
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")

    // 3. Logging API (Für @Slf4j in Use Cases)
    implementation("org.slf4j:slf4j-api")
}