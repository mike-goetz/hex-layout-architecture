# STAGE 1: Build
FROM gradle:8.12-jdk21 AS builder
WORKDIR /app
COPY . .
# Da wir Java 26 via Toolchain im Build-File konfiguriert haben, lädt Gradle
# das passende JDK während des Kompiliervorgangs selbst herunter.
RUN ./gradlew :travel-core:infrastructure:bootJar --no-daemon

# STAGE 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Port freigeben (Standard Spring Port)
EXPOSE 8080
# Kopiert das klassifizierte jar
COPY --from=builder /app/travel-core/infrastructure/build/libs/*-core.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]