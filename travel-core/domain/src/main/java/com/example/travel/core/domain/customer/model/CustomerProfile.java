package com.example.travel.core.domain.customer.model;

import lombok.Data;

@Data
public class CustomerProfile {
    private String uid;
    private String customerId;
    private boolean vip;
}