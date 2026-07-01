package com.example.travel.core.infrastructure.holiday.adapter.out.hotel;

import com.example.travel.core.business.holiday.port.HotelServicePort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("bookingCom")
@RequiredArgsConstructor
public class BookingComAdapter implements HotelServicePort {

    @Override
    public String bookHotel(String customerId, String destination, List<HolidayBooking.AddOn> addOns) {
        return "BOOK-12493520";
    }

    @Override
    public void cancelHotel(String hotelConfirmationId) {

    }
}
