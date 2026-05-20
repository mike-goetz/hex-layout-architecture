package com.example.travel.core.infrastructure.customer.adapter.out.persistence;

import com.example.travel.core.domain.customer.model.CustomerProfile;
import com.example.travel.core.domain.holiday.CustomerProfilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerProfilePort {

    private final CustomerProfileSpringDataRepo jpaRepo;
    private final CustomerPersistenceMapper mapper;

    @Override
    public Optional<CustomerProfile> findByCustomerId(String customerId) {
        return jpaRepo.findByCustomerId(customerId).map(mapper::toDomain);
    }
}
