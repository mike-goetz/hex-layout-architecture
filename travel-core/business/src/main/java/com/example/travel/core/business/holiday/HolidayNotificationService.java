package com.example.travel.core.business.holiday;

import com.example.travel.core.domain.holiday.model.HolidayBooking;
import com.example.travel.core.domain.port.AuditLogPort;
import com.example.travel.core.domain.port.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayNotificationService {

    private final NotificationPort notificationPort;
    private final AuditLogPort auditLogPort;

    // Diese Logik ist Orchestrierung (Business), keine reine Fachlogik (Domain)
    public void notifyCustomerAndLog(HolidayBooking booking, String message) {
        notificationPort.sendSms(booking.getCustomerId(), message);

        if (booking.getStatus() == HolidayBooking.BookingStatus.FAILED) {
            auditLogPort.logCriticalFailure("Booking failed for customer: " + booking.getCustomerId());
        } else {
            auditLogPort.logSuccess("Booking successful: " + booking.getUid());
        }
    }
}