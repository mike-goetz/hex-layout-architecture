package com.example.travel.core.infrastructure.holiday.adapter.out.hotel;

import com.example.travel.core.domain.holiday.HotelServicePort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import com.example.travel.core.infrastructure.config.tenant.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary // Teilt Spring-DI mit, dass dieser Proxy als primärer Bean für HotelServicePort genutzt werden soll
@Slf4j
public class HotelServiceRoutingProxy implements HotelServicePort {

    private final HotelServicePort bookingComProvider;
    private final HotelServicePort trivagoProvider;
    private final String defaultProvider;

    public HotelServiceRoutingProxy(
            @Qualifier("bookingCom") HotelServicePort bookingComProvider,
            @Qualifier("trivago") HotelServicePort trivagoProvider,
            @Value("${travel.hotel.provider:bookingCom}") String defaultProvider) {
        this.bookingComProvider = bookingComProvider;
        this.trivagoProvider = trivagoProvider;
        this.defaultProvider = defaultProvider;
    }

    @Override
    public String bookHotel(String customerId, String destination, List<HolidayBooking.AddOn> addOns) {
        HotelServicePort activeProvider = resolveActiveProvider();
        return activeProvider.bookHotel(customerId, destination, addOns);
    }

    @Override
    public void cancelHotel(String hotelConfirmationId) {
        HotelServicePort activeProvider = resolveActiveProvider();
        activeProvider.cancelHotel(hotelConfirmationId);
    }

    /**
     * Ermittelt den aktiven Provider anhand von Tenant-Regeln oder globalen Feature-Flags.
     */
    private HotelServicePort resolveActiveProvider() {
        String currentTenant = TenantContext.getTenantId();

        // Beispiel 1: Mandantenspezifisches Routing (Tenant-Aware Routing)
        if ("tenant_c1001".equals(currentTenant)) {
            log.debug("Tenant {} aktiv. Route zu: trivago", currentTenant);
            return trivagoProvider;
        }

        if ("tenant_c1002".equals(currentTenant)) {
            log.debug("Tenant {} aktiv. Route zu: bookingCom", currentTenant);
            return bookingComProvider;
        }

        // Beispiel 2: Fallback auf das globale Feature-Flag (aus application.yml / Togglz)
        log.debug("Standard-Routing aktiv. Route zu konfiguriertem Provider: {}", defaultProvider);
        if ("trivago".equalsIgnoreCase(defaultProvider)) {
            return trivagoProvider;
        }

        return bookingComProvider;
    }
}