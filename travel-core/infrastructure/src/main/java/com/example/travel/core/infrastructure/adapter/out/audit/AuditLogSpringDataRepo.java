package com.example.travel.core.infrastructure.adapter.out.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogSpringDataRepo extends JpaRepository<AuditLogJpaEntity, Long> {
}