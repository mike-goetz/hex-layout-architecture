package com.example.travel.core.infrastructure.holiday.adapter.out.persistence;

import com.example.travel.core.domain.holiday.model.HolidayBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingPersistenceMapper {

    /**
     * Domain -> JPA
     * Ignoriert die interne DB-ID. Die 'uid' (uid) wird automatisch gemappt,
     * da die Felder in Domain und JPA den gleichen Namen tragen ('uid').
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    BookingJpaEntity toJpaEntity(HolidayBooking domain);

    /**
     * JPA -> Domain
     * MapStruct liest die Strings aus der Datenbank aus und wandelt sie ggf.
     * in die passenden Enums im HolidayBooking-Objekt um.
     */
    HolidayBooking toDomain(BookingJpaEntity entity);
}