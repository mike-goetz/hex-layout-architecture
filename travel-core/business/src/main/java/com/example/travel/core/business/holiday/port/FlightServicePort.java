package com.example.travel.core.business.holiday.port;

public interface FlightServicePort {
    String bookFlight(String customerId, String destination);

    void cancelFlight(String flightConfirmationId);
}