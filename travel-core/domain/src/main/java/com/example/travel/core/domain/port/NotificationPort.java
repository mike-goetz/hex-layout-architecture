package com.example.travel.core.domain.port;

public interface NotificationPort {
    /**
     * Sendet eine SMS an den Kunden.
     */
    void sendSms(String customerId, String message);

    /**
     * Sendet eine E-Mail an den Kunden.
     */
    void sendEmail(String customerId, String subject, String body);
}