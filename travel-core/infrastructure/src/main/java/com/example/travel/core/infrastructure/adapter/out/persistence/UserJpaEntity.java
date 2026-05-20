package com.example.travel.core.infrastructure.adapter.out.persistence;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Set;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(
        name = "app_user",
        indexes = {@Index(name = "UK_USER__USERNAME", columnList = "username", unique = true)}
)
@Data
public class UserJpaEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    @Column(nullable = false)
    private long version = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_USER__CREATED_BY"))
    private UserJpaEntity createdBy;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_USER__MODIFIED_BY"))
    private UserJpaEntity lastModifiedBy;

    @Column(nullable = false)
    private String username;

    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_id", foreignKey = @ForeignKey(name = "FK_USER__CONTACT"))
    private ContactJpaEntity contact;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "app_user_role",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER_ROLES__USER")),
            inverseJoinColumns = @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_USER_ROLES__ROLE")))
    private Set<RoleJpaEntity> roles;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
