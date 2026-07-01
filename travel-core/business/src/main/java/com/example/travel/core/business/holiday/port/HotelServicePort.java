package com.example.travel.core.business.holiday.port;

import com.example.travel.core.domain.holiday.model.HolidayBooking;

import java.util.List;

public interface HotelServicePort {
    String bookHotel(String customerId, String destination, List<HolidayBooking.AddOn> addOns);

    void cancelHotel(String hotelConfirmationId);
}