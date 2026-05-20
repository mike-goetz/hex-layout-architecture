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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(
        name = "app_role",
        indexes = {@Index(name = "UK_ROLE__UID", columnList = "uid", unique = true)}
)
@Data
public class RoleJpaEntity implements Persistable<Long> {

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
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_ROLE__CREATED_BY"))
    private UserJpaEntity createdBy;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_ROLE__MODIFIED_BY"))
    private UserJpaEntity lastModifiedBy;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private boolean active = true;

    private Integer sortOrder;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "app_role_permission",
            joinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_ROLE_PREMISSIONS__ROLE")),
            inverseJoinColumns = @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(name = "FK_ROLE_PREMISSIONS__REMISSION")))
    private Set<PermissionJpaEntity> permissions;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
