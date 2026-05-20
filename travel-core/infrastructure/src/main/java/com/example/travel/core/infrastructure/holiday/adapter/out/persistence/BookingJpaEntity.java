package com.example.travel.core.infrastructure.holiday.adapter.out.persistence;

import com.example.travel.core.infrastructure.adapter.out.persistence.UserJpaEntity;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(
        name = "holiday_booking",
        indexes = {@Index(name = "UK_HOLIDAY_BOOKING__UID", columnList = "uid", unique = true)}
)
@Data
public class BookingJpaEntity implements Persistable<Long> {

    // 1. SURROGATE KEY (Datenbank-intern, für performante JOINs)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // 2. BUSINESS KEY (Die echte Identität aus der Domain)
    @Column(name = "uid", unique = true, nullable = false, updatable = false)
    private String uid;

    @Version
    @Column(nullable = false)
    private long version = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_HOLIDAY_BOOKING__CREATED_BY"))
    private UserJpaEntity createdBy;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_HOLIDAY_BOOKING__MODIFIED_BY"))
    private UserJpaEntity lastModifiedBy;

    private String customerId;
    private String destination;

    @Column(name = "status")
    private String status; // Wird als String in DB gespeichert (PENDING, CONFIRMED, etc.)

    private BigDecimal totalPrice;

    private String flightConfirmationId;
    private String hotelConfirmationId;

    // Einfache Collection-Table für die AddOns (SPA, ALL_INCLUSIVE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "holiday_booking_addon", joinColumns = @JoinColumn(name = "booking_id"), foreignKey = @ForeignKey(name = "FK_HOLIDAY_BOOKING__ADDON"))
    @Column(name = "add_on")
    private List<String> addOns;

    @Override
    public boolean isNew() {
        return id == null;
    }
}