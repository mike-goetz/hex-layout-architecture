package com.example.travel.core.infrastructure.holiday.adapter.out.persistence;

import com.example.travel.core.domain.holiday.HolidayBookingPort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingRepositoryAdapter implements HolidayBookingPort {

    private final BookingSpringDataRepo jpaRepo;
    private final BookingPersistenceMapper mapper;

    @Override
    public HolidayBooking save(HolidayBooking booking) {
        BookingJpaEntity entity = mapper.toJpaEntity(booking);
        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<HolidayBooking> findByUid(String bookingUid) {
        return jpaRepo.findByUid(bookingUid).map(mapper::toDomain);
    }
}