package com.example.travel.core.infrastructure;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("test") // Aktiviert die application-test.yml
@Testcontainers
class TravelCoreServiceTest implements WithAssertions {

    @Container
    @ServiceConnection // Spring Boot 4 bindet die Connection automatisch an die Datasource an
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Test
    void contextLoads() {
        assertThat(postgres.isRunning()).isTrue();
    }

}