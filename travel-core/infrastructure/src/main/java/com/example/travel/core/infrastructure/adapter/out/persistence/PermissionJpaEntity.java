package com.example.travel.core.infrastructure.adapter.out.persistence;

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
        name = "app_permission",
        indexes = {@Index(name = "UK_PERMISSION__UID", columnList = "uid", unique = true)}
)
@Data
public class PermissionJpaEntity implements Persistable<Long> {

    // 1. SURROGATE KEY (Datenbank-intern, für performante JOINs)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // 2. BUSINESS KEY (Die echte Identität aus der Domain)
    @Column(unique = true, nullable = false, updatable = false)
    private String uid;

    @Version
    @Column(nullable = false)
    private long version = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_PERMISSION__CREATED_BY"))
    private UserJpaEntity createdBy;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_PERMISSION__MODIFIED_BY"))
    private UserJpaEntity lastModifiedBy;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private boolean active = true;

    private Integer sortOrder;

    @Override
    public boolean isNew() {
        return id == null;
    }

}
