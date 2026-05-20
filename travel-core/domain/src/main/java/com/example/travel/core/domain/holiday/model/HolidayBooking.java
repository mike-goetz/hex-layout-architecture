package com.example.travel.core.domain.holiday.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class HolidayBooking {
    private String uid;
    private String customerId;
    private String destination;
    private BookingStatus status;
    private BigDecimal totalPrice;

    // Externe Referenzen
    private String flightConfirmationId;
    private String hotelConfirmationId;
    private List<AddOn> addOns = new ArrayList<>();

    public enum BookingStatus {PENDING, CONFIRMED, FAILED, CANCELLED}

    public enum AddOn {SPA, ALL_INCLUSIVE, LATE_CHECKOUT}

    public void confirm(String flightId, String hotelId) {
        this.flightConfirmationId = flightId;
        this.hotelConfirmationId = hotelId;
        this.status = BookingStatus.CONFIRMED;
    }

    public void markAsFailed() {
        this.status = BookingStatus.FAILED;
    }
}