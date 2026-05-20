package com.example.travel.core.infrastructure.holiday.adapter.out.flight;

import com.example.travel.core.domain.holiday.FlightServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwissFlightServiceAdapter implements FlightServicePort {

    @Override
    public String bookFlight(String customerId, String destination) {
        // Echter HTTP-Call (z.B. an Swiss oder Lufthansa)
        // ResponseEntity<FlightResponse> response = restTemplate.postForEntity(...);
        // return response.getBody().getBookingReference();
        return "FLIGHT-REF-12345";
    }

    @Override
    public void cancelFlight(String flightConfirmationId) {
        // restTemplate.delete("https://api.swiss.com/v1/bookings/" + flightConfirmationId);
        log.info("Flug {} erfolgreich storniert (Saga Kompensation).", flightConfirmationId);
    }
}