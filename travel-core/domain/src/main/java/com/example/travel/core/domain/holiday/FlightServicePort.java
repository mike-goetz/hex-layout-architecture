package com.example.travel.core.domain.holiday;

public interface FlightServicePort {
    String bookFlight(String customerId, String destination);

    void cancelFlight(String flightConfirmationId);
}