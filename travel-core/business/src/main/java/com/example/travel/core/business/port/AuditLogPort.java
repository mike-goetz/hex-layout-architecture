package com.example.travel.core.business.port;

public interface AuditLogPort {
    /**
     * Protokolliert eine erfolgreiche Transaktion oder Aktion im Audit-Log.
     */
    void logSuccess(String message);

    /**
     * Protokolliert einen kritischen Fehler oder einen Abbruch im Audit-Log.
     */
    void logCriticalFailure(String message);
}