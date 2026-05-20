package com.example.travel.core.infrastructure.adapter.out.persistence;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "contact")
@Data
public class ContactJpaEntity implements Persistable<Long> {

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
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_CONTACT__CREATED_BY"))
    private UserJpaEntity createdBy;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_CONTACT__MODIFIED_BY"))
    private UserJpaEntity lastModifiedBy;

    private String firstname;

    private String lastname;

    private String email;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
