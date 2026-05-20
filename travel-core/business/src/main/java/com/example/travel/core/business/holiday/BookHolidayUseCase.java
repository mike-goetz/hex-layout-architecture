package com.example.travel.core.business.holiday;

import com.example.travel.core.domain.customer.model.CustomerProfile;
import com.example.travel.core.domain.holiday.CustomerProfilePort;
import com.example.travel.core.domain.holiday.FlightServicePort;
import com.example.travel.core.domain.holiday.HolidayBookingPort;
import com.example.travel.core.domain.holiday.HotelServicePort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookHolidayUseCase {

    private final HolidayBookingPort bookingPort;
    private final CustomerProfilePort customerPort;
    private final FlightServicePort flightPort;
    private final HotelServicePort hotelPort;
    private final HolidayNotificationService notificationService; // Injected Business Service
    private final HolidayPricingService pricingService; // Injected Business Service

    public HolidayBooking execute(String customerId, String destination, List<HolidayBooking.AddOn> addOns) {

        CustomerProfile customer = customerPort.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Kunde nicht gefunden"));

        HolidayBooking booking = new HolidayBooking();
        booking.setUid(UUID.randomUUID().toString());
        booking.setCustomerId(customerId);
        booking.setDestination(destination);
        booking.setAddOns(addOns);
        booking.setStatus(HolidayBooking.BookingStatus.PENDING);

        BigDecimal finalPrice = pricingService.calculateTotal(booking, customer);
        booking.setTotalPrice(finalPrice);

        bookingPort.save(booking);

        String flightId = null;
        String hotelId = null;

        try {
            flightId = flightPort.bookFlight(customerId, destination);
            hotelId = hotelPort.bookHotel(customerId, destination, addOns);

            booking.confirm(flightId, hotelId);
            HolidayBooking confirmedBooking = bookingPort.save(booking);

            // Trigger notification on success
            notificationService.notifyCustomerAndLog(confirmedBooking, "Ihre Ferienbuchung nach " + destination + " wurde erfolgreich bestätigt.");

            return confirmedBooking;

        } catch (Exception e) {
            log.error("Buchung fehlgeschlagen für UID: {}", booking.getUid(), e);

            if (flightId != null && hotelId == null) {
                try {
                    flightPort.cancelFlight(flightId);
                } catch (Exception cancelEx) {
                    log.error("KRITISCH: Kompensation fehlgeschlagen!", cancelEx);
                }
            }

            booking.markAsFailed();
            HolidayBooking failedBooking = bookingPort.save(booking);

            // Trigger notification on failure
            notificationService.notifyCustomerAndLog(failedBooking, "Ferienbuchung fehlgeschlagen: " + e.getMessage());

            throw new BookingFailedException("Ferienbuchung fehlgeschlagen.", e);
        }
    }
}