package com.example.travel.core.infrastructure.holiday.adapter.in.web;

import com.example.travel.core.business.holiday.BookHolidayUseCase;
import com.example.travel.core.business.holiday.CancelHolidayUseCase;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingRequest;
import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayRestService {

    private final BookHolidayUseCase bookHolidayUseCase;
    private final CancelHolidayUseCase cancelHolidayUseCase;
    private final HolidayRestMapper mapper;

    /**
     * Nimmt den validierten REST-Request entgegen, bereitet die Parameter vor
     * und delegiert die Geschäftslogik an den Use Case.
     */
    public HolidayBookingResponse book(String customerId, HolidayBookingRequest request) {

        // 1. Mapping & Parameter-Aufbereitung
        String destination = request.getDestination();
        List<HolidayBooking.AddOn> addOns = request.getAddOns() != null ? request.getAddOns() : List.of();

        // 2. Aufruf der Geschäftslogik
        HolidayBooking domainResult = bookHolidayUseCase.execute(customerId, destination, addOns);

        // 3. Mapping: Domain -> Response-DTO
        return mapper.toResponse(domainResult);
    }
}