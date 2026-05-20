package com.example.travel.core.business.holiday;


import com.example.travel.core.domain.customer.model.CustomerProfile;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayPricingService {

    public BigDecimal calculateTotal(HolidayBooking booking, CustomerProfile customer) {
        BigDecimal basePrice = getBasePrice(booking.getDestination());
        BigDecimal addOnPrice = calculateAddOns(booking.getAddOns());

        BigDecimal total = basePrice.add(addOnPrice);

        // Business Rule: VIP-Kunden erhalten 10% Rabatt auf den Gesamtpreis
        if (customer.isVip()) {
            total = total.multiply(new BigDecimal("0.90"));
        }

        return total;
    }

    private BigDecimal getBasePrice(String destination) {
        return "Malediven".equalsIgnoreCase(destination) ? new BigDecimal("2000") : new BigDecimal("1000");
    }

    private BigDecimal calculateAddOns(List<HolidayBooking.AddOn> addOns) {
        BigDecimal sum = BigDecimal.ZERO;
        if (addOns != null) {
            for (HolidayBooking.AddOn addOn : addOns) {
                if (addOn == HolidayBooking.AddOn.SPA) sum = sum.add(new BigDecimal("200"));
                if (addOn == HolidayBooking.AddOn.ALL_INCLUSIVE) sum = sum.add(new BigDecimal("500"));
            }
        }
        return sum;
    }
}