package com.example.travel.core.infrastructure.holiday.adapter.in.web.dto;

import com.example.travel.core.domain.holiday.model.HolidayBooking;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "Payload für eine neue Ferienbuchung")
public class HolidayBookingRequest {

    @NotBlank
    @Schema(description = "Zielort für den Urlaub", example = "Malediven")
    private String destination;

    @Schema(description = "Gewünschte Zusatzleistungen (z.B. SPA, ALL_INCLUSIVE)")
    private List<HolidayBooking.AddOn> addOns;
}