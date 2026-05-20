package com.example.travel.core.domain.holiday;


import com.example.travel.core.domain.customer.model.CustomerProfile;

import java.util.Optional;

public interface CustomerProfilePort {
    Optional<CustomerProfile> findByCustomerId(String customerId);
}