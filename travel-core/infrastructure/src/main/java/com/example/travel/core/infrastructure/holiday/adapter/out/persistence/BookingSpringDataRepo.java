package com.example.travel.core.infrastructure.holiday.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingSpringDataRepo extends JpaRepository<BookingJpaEntity, Long> {

    // Wir suchen NICHT über den DB-Primary Key, sondern über den Business Key (UID)
    Optional<BookingJpaEntity> findByUid(String uid);

    void deleteByUid(String uid);
}