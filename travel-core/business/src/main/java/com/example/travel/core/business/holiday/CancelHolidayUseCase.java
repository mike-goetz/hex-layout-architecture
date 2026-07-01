package com.example.travel.core.business.holiday;

import com.example.travel.core.business.holiday.port.FlightServicePort;
import com.example.travel.core.business.holiday.port.HolidayBookingPort;
import com.example.travel.core.business.holiday.port.HotelServicePort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CancelHolidayUseCase {

    private final HolidayBookingPort bookingPort;
    private final FlightServicePort flightPort;
    private final HotelServicePort hotelPort;

    // An example of injecting a "Business Service" for shared orchestration (like sending emails)
    private final HolidayNotificationService notificationService;

    public HolidayBooking execute(String bookingUid) {
        log.info("Initiating cancellation for booking UID: {}", bookingUid);

        // 1. Load the Aggregate Root
        // Assuming findByUid is defined in the HolidayBookingRepository port
        HolidayBooking booking = bookingPort.findByUid(bookingUid)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingUid));

        // 2. Check current status (Domain Logic validation)
        if (booking.getStatus() == HolidayBooking.BookingStatus.CANCELLED) {
            log.info("Booking {} is already cancelled. Ignoring request.", bookingUid);
            return booking;
        }

        // 3. Cancel external dependencies via Ports
        try {
            if (booking.getFlightConfirmationId() != null) {
                log.info("Cancelling flight: {}", booking.getFlightConfirmationId());
                flightPort.cancelFlight(booking.getFlightConfirmationId());
            }

            if (booking.getHotelConfirmationId() != null) {
                log.info("Cancelling hotel: {}", booking.getHotelConfirmationId());
                // Assuming cancelHotel exists on the HotelServicePort
                hotelPort.cancelHotel(booking.getHotelConfirmationId());
            }
        } catch (Exception e) {
            log.error("Failed to cancel external bookings for UID: {}", bookingUid, e);
            // In a highly resilient system, you might mark it as "CANCELLATION_PENDING" 
            // and retry via a background job, but here we throw to abort the transaction.
            throw new BookingFailedException("Failed to fully cancel the holiday with external providers.", e);
        }

        // 4. Update the Domain Model State
        booking.setStatus(HolidayBooking.BookingStatus.CANCELLED);

        // 5. Persist the updated state to the local DB
        HolidayBooking savedBooking = bookingPort.save(booking);

        // 6. Optional: Call a Business Service for shared orchestration
        notificationService.notifyCustomerAndLog(savedBooking, "Your holiday has been successfully cancelled.");

        return savedBooking;
    }
}