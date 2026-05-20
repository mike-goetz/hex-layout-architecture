package com.example.travel.core.infrastructure.customer.adapter.out.persistence;

import com.example.travel.core.infrastructure.adapter.out.persistence.UserJpaEntity;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
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
import java.time.Instant;

@Entity
@Table(
        name = "customer_profile",
        indexes = {@Index(name = "UK_CUSTOMER_PROFILE__UID", columnList = "uid", unique = true)}
)
@Data
public class CustomerProfileJpaEntity implements Persistable<Long> {

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
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_CUSTOMER_PROFILE__CREATED_BY"))
    private UserJpaEntity createdBy;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_CUSTOMER_PROFILE__MODIFIED_BY"))
    private UserJpaEntity lastModifiedBy;

    private String customerId;
    private boolean vip;

    @Override
    public boolean isNew() {
        return id == null;
    }
}