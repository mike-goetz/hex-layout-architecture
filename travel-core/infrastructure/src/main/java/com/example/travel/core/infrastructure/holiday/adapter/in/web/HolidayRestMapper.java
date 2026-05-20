package com.example.travel.core.infrastructure.holiday.adapter.in.web;

import com.example.travel.core.domain.holiday.model.HolidayBooking;
import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HolidayRestMapper {

    /**
     * Übersetzt das intern genutzte Domain-Modell in das API-Response-Format.
     * Da die Felder gleich heißen, findet MapStruct die Zuordnung automatisch.
     */
    HolidayBookingResponse toResponse(HolidayBooking domain);
}