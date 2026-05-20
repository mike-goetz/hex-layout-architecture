package com.example.travel.core.infrastructure.adapter.out.notification;

import com.example.travel.core.domain.port.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationAdapter implements NotificationPort {

    @Override
    public void sendSms(String customerId, String message) {
        // In Produktion: Aufruf eines SMS-Gateway-Dienstes (z.B. Twilio)
        log.info("SMS gesendet an Kunde {}: {}", customerId, message);
    }

    @Override
    public void sendEmail(String customerId, String subject, String body) {
        // In Produktion: Aufruf eines Mail-Dienstes via JavaMailSender oder externer API (z.B. SendGrid)
        log.info("E-Mail gesendet an Kunde {} | Betreff: {} | Inhalt: {}", customerId, subject, body);
    }
}