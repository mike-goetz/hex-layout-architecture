package com.example.travel.core.infrastructure.adapter.out.audit;

import com.example.travel.core.business.port.AuditLogPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAdapter implements AuditLogPort {

    private final AuditLogSpringDataRepo auditLogRepo;

    @Override
    public void logSuccess(String message) {
        log.info("AUDIT SUCCESS: {}", message);
        saveAuditLog("SUCCESS", message);
    }

    @Override
    public void logCriticalFailure(String message) {
        log.error("AUDIT CRITICAL: {}", message);
        saveAuditLog("CRITICAL", message);
    }

    private void saveAuditLog(String severity, String message) {
        try {
            AuditLogJpaEntity entity = new AuditLogJpaEntity();
            entity.setSeverity(severity);
            entity.setMessage(message);
            auditLogRepo.save(entity);
        } catch (Exception e) {
            // Ein fehlschlagendes Audit-Log darf niemals die Haupt-Transaktion abbrechen lassen
            log.error("KRITISCH: Audit-Eintrag konnte nicht persistiert werden!", e);
        }
    }
}