package com.example.travel.core.infrastructure.holiday.adapter.in.web.dto;

import com.example.travel.core.domain.holiday.model.HolidayBooking;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Ergebnis der verarbeiteten Ferienbuchung")
public class HolidayBookingResponse {

    @Schema(description = "Eindeutige ID der Buchung (TSID)", example = "0DHKZW9Q")
    private String uid;

    @Schema(description = "ID des Kunden", example = "CUST-1234")
    private String customerId;

    @Schema(description = "Zielort", example = "Malediven")
    private String destination;

    @Schema(description = "Aktueller Status der Buchung", example = "CONFIRMED")
    private HolidayBooking.BookingStatus status;

    @Schema(description = "Berechneter Gesamtpreis", example = "2700.00")
    private BigDecimal totalPrice;

    @Schema(description = "Referenznummer des gebuchten Fluges", example = "FLIGHT-REF-12345")
    private String flightConfirmationId;

    @Schema(description = "Referenznummer des gebuchten Hotels")
    private String hotelConfirmationId;

    @Schema(description = "Zusatzleistungen")
    private List<HolidayBooking.AddOn> addOns;
}