package com.example.travel.core.infrastructure.config.tenant;

import lombok.Setter;
import org.flywaydb.core.Flyway;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "multitenancy")
public class FlywayMultiTenantInitializer {

    private final DataSource dataSource;
    @Setter
    private List<String> tenants = new ArrayList<>();

    public FlywayMultiTenantInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrateTenants() throws SQLException {
//        if(true) return;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            for (String tenant : tenants) {
                // 1. Schema in PostgreSQL anlegen, falls es nicht existiert
                statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenant);

                // 2. Flyway für dieses Schema konfigurieren und starten
                Flyway.configure()
                        .dataSource(dataSource)
                        .schemas(tenant)
                        .locations("classpath:db/migration/postgres")
                        .load()
                        .migrate();
            }
        }
    }
}