package com.example.travel.core.domain.holiday;


import com.example.travel.core.domain.holiday.model.HolidayBooking;

import java.util.Optional;

public interface HolidayBookingPort {
    HolidayBooking save(HolidayBooking booking);

    Optional<HolidayBooking> findByUid(String bookingUid);
}