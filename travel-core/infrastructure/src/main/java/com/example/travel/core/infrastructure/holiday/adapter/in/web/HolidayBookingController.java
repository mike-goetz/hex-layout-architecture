package com.example.travel.core.infrastructure.holiday.adapter.in.web;

import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingRequest;
import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday Booking", description = "Endpunkte für Ferienbuchungen")
public class HolidayBookingController {

    private final HolidayRestService restService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neue Ferien buchen (Flug + Hotel + AddOns)")
    public HolidayBookingResponse book(
            @RequestHeader("X-Customer-Id") String customerId,
            @Valid @RequestBody HolidayBookingRequest request) {

        // Direkte Delegation an den Rest Service
        return restService.book(customerId, request);
    }
}