package com.example.travel.core.infrastructure.config.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
    // Registriert den Auditor-Mechanismus über das referenzierte Bean "auditorProvider"
}