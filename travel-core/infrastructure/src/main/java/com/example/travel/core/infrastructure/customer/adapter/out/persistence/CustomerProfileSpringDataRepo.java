package com.example.travel.core.infrastructure.customer.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerProfileSpringDataRepo extends JpaRepository<CustomerProfileJpaEntity, Long> {

    Optional<CustomerProfileJpaEntity> findByCustomerId(String customerId);
}