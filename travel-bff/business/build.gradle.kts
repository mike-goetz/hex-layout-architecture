dependencies {
    // 1. Abhängigkeit nach innen: Business MUSS Domain kennen
    implementation(project(":travel-bff:domain"))

    // 2. Spring Basics
    implementation("org.springframework:spring-context")
    implementation("org.slf4j:slf4j-api")
}