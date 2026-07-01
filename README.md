# Hexagonal Architecture Implementation (Ports & Adapters)

This document describes the implementation of the hexagonal architecture (Ports & Adapters pattern) within the **travel-core** service. The structure follows the physical control flow of an incoming API request: from the outer Inbound Infrastructure, through the Business Orchestration layer into the pure Domain Core, and finally out to the Outbound Adapters (Persistence and Third-Party APIs).

## Layer Overview (Control Flow)

### Color-Coding Legend for Submodules & Layers

| Layer / Submodule | Visuelle Farbe (1:1 Mermaid Style) | CSS-Hexcode | Beschreibung & Enthaltene Komponenten | 
 | ----- | ----- | ----- | ----- | 
| **Domain** | <span style="background-color:#fffde7; border:2px solid #fbc02d; color:#1a1a1a; padding:4px 8px; border-radius:4px; font-weight:bold; display:inline-block;">🟡 Domain</span> | BG: `#fffde7`<br>Border: `#fbc02d` | Reine Geschäftslogik ohne Framework-Abhängigkeiten. Beinhaltet Aggregate, Entities, Enums und Outbound-Port-Interfaces (`*-domain` Module). | 
| **Business** | <span style="background-color:#e8f5e9; border:2px solid #388e3c; color:#1a1a1a; padding:4px 8px; border-radius:4px; font-weight:bold; display:inline-block;">🟢 Business</span> | BG: `#e8f5e9`<br>Border: `#388e3c` | Orchestrierungsebene, Transaktionsgrenzen und UseCases. Beinhaltet fachliche Sagas und zustandslose Business Services (`*-business` Module). | 
| **Infrastructure** | <span style="background-color:#e1f5fe; border:2px solid #0288d1; color:#1a1a1a; padding:4px 8px; border-radius:4px; font-weight:bold; display:inline-block;">🔵 Infrastructure</span> | BG: `#e1f5fe`<br>Border: `#0288d1` | Technische Adapter, REST Controller, JPA Entities, Flyway-Konfigurationen und 3rd-Party-REST-Clients (`*-infrastructure` Module). | 
```mermaid
graph TD
    subgraph Inbound ["1. Inbound Infrastructure (Outside)"]
        A[HolidayBookingController] -->|Delegates to| B[HolidayRestService]
    end

    subgraph Business ["2. Business Layer (Orchestration)"]
        B -->|Executes Use Case| C[BookHolidayUseCase]
        C -->|Invokes stateless calculation| E[HolidayPricingBusinessService]
        C -->|Orchestrates alerts & logging| N[HolidayNotificationBusinessService]
    end

    subgraph Ports ["4. Outbound Ports (Interfaces)"]
        C -->|Invokes flight booking| F[FlightServicePort]
        C -->|Invokes hotel booking| G[HotelServicePort]
        C -->|Saves / Loads via Port| H[HolidayBookingPort]
        C -->|Loads Profile via Port| CP[CustomerProfilePort]
        N -->|Sends message| NP[NotificationPort]
        N -->|Writes log| ALP[AuditLogPort]
    end

    subgraph Outbound ["5. Outbound Adapters (Outside)"]
        F -->|Implemented by| I[FlightServiceAdapter]
        G -->|Implemented by Proxy| J_Proxy[HotelServiceRoutingProxy]
        J_Proxy -->|Delegates| J_BC[BookingComAdapter]
        J_Proxy -->|Delegates| J_TV[TrivagoAdapter]
        H -->|Implemented by| K[BookingRepositoryAdapter]
        CP -->|Implemented by| CPA[CustomerProfileRepositoryAdapter]
        NP -->|Implemented by| NPA[NotificationAdapter]
        ALP -->|Implemented by| ALA[AuditLogAdapter]
    end

    subgraph Persist ["6a. Persistence Layer (Database)"]
        K -->|Saves/Loads| DB_Booking[(BookingJpaEntity)]
        CPA -->|Reads| DB_Profile[(CustomerProfileJpaEntity)]
        ALA -->|Saves| DB_Audit[(AuditLogJpaEntity)]
    end

    subgraph Clients ["6b. Outbound Client Calls (3rd Party APIs)"]
        I -->|HTTP REST| CC_Swiss[Swiss Flight Client]
        NPA -->|HTTP REST| CC_Notif[Notification Client]
        J_BC -->|HTTP REST| CC_BC[Booking.com API Client]
        J_TV -->|HTTP REST| CC_TV[Trivago API Client]
    end

    classDef domain fill:#fffde7,stroke:#fbc02d,stroke-width:2px;
    classDef business fill:#e8f5e9,stroke:#388e3c,stroke-width:2px;
    classDef infra fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;

    class A,B,I,J_Proxy,J_BC,J_TV,K,CPA,NPA,ALA,DB_Booking,DB_Profile,DB_Audit,CC_Swiss,CC_Notif,CC_BC,CC_TV infra;
    class C,E,N business;
    class F,G,H,CP,NP,ALP domain;
```

## Monorepo Project Structure

The project is organized as a multi-module Gradle Monorepo, decoupling the Frontend, the Backend-for-Frontend (BFF), and the Core Backend Service.

### Submodule Dependency Graph

```mermaid
graph TD
    subgraph travel-ui [Frontend Layer]
        UI[travel-ui Angular SPA]
    end

    subgraph travel-bff [Backend For Frontend - BFF]
        BFF-Infra[travel-bff-infrastructure] -->|depends on| BFF-Biz[travel-bff-business]
        BFF-Biz -->|depends on| BFF-Dom[travel-bff-domain]
        BFF-Infra -->|depends on| BFF-Dom[travel-bff-domain]
    end

    subgraph travel-core [Core Business Service]
        Core-Infra[travel-core-infrastructure] -->|depends on| Core-Biz[travel-core-business]
        Core-Biz -->|depends on| Core-Dom[travel-core-domain]
        Core-Infra -->|depends on| Core-Dom[travel-core-domain]
    end

    UI -->|REST / JSON| BFF-Infra
    BFF-Infra -->|WebClient REST Calls| Core-Infra

    classDef ui fill:#eceff1,stroke:#455a64,stroke-width:2px;
    classDef domain fill:#fffde7,stroke:#fbc02d,stroke-width:2px;
    classDef business fill:#e8f5e9,stroke:#388e3c,stroke-width:2px;
    classDef infra fill:#e1f5fe,stroke:#0288d1,stroke-width:2px;

    class UI ui;
    class BFF-Dom,Core-Dom domain;
    class BFF-Biz,Core-Biz business;
    class BFF-Infra,Core-Infra infra;
```

### File & Folder Architecture

```text
travel/
├── settings.gradle.kts             # Declares all subprojects
├── build.gradle.kts                # Centralized root configuration & toolchains
│
├── travel-ui/                      # Angular Single Page Application (Frontend)
│   └── src/                        # Components, services and templates
│
├── travel-bff/                     # Gateway BFF (aggregates APIs & handles sessions)
│   ├── domain/                     # BFF-specific client mapping contracts
│   ├── business/                   # Lightweight aggregation logic
│   └── infrastructure/             # WebClient calling travel-core, Security Config (Depends on business & domain)
│
└── travel-core/                    # Central Business Domain Microservice
    ├── domain/                     # Pure business logic, Aggregate Root (No frameworks!)
    ├── business/                   # Transactional Orchestration Layer, UseCases, Ports (Depends on domain)
    └── infrastructure/             # Heavy Framework dependencies (Spring, JPA Entities, Flyway, PostgreSQL - Depends on business & domain)
```

## Architectural Elements & Boundaries Deep Dive

To prevent architectural decay, we enforce strict boundaries for each structural element from outside to inside:

| Element | Location | Responsibility | Constraints | 
 | ----- | ----- | ----- | ----- | 
| **Inbound Adapter (Controller)** | `infrastructure` | Listens to a technical protocol (HTTP/REST, gRPC, CLI). Handles payload routing, JSON parsing, HTTP response codes, and API-spec annotations. | No business logic. Must immediately delegate to the Inbound Service or UseCase. | 
| **Inbound Service (RestService)** | `infrastructure` | Prepares the parameters for the Use Case. Handles technical-level validations, security context extractions, and maps $\text{DTOs} \leftrightarrow \text{Domain Models}$ (via MapStruct). | No transactional logic. Acts strictly as a translation layer before touching the Business module. | 
| **Use Case (Business Service)** | `business` | The transaction boundary. Orchestrates the business workflow (Saga pattern, step orchestration). Loads Aggregates, invokes Business Services, saves modifications via Ports, and handles technical compensations. | Framework agnostic (ideally). No database dependencies, Web dependencies, or specific JSON annotations. It communicates only via abstract Ports. | 
| **Business Service (e.g., Pricing, Notification)** | `business` | Pure stateless business operations. Contains complex business calculations (e.g., pricing, taxation) or coordinates notifications and logs that cross-cut several domains. | No direct database access. Must access persistence or external systems only through Ports if needed. Pricing has no Ports. | 
| **Aggregate Root (Domain Entity)** | `domain` | Rich entity capturing state, core rules, and behavior. Maintains consistency boundaries. | Zero annotations from ORMs (No JPA, No Jackson). Pure Java/Kotlin. | 
| **Outbound Port** | `domain` or `business` | An interface describing what external capability (DB persistence, REST clients, SMS gateways) is required by the business logic. | Must use the suffix `Port` (e.g., `HolidayBookingRepositoryPort`). Never leaks infrastructure types. | 
| **Outbound Adapter** | `infrastructure` | Technical implementation of an Outbound Port. Talks to H2, PostgreSQL, Redis, external REST endpoints, etc. | Maps external technology schemas to internal domain structures so changes in DB/APIs never break the Domain Core. | 

## 1. The Inbound Infrastructure (Outside)

This layer intercepts HTTP requests, validates incoming payloads, and transforms network transport objects (DTOs) into an application-friendly format. It must never contain business rules or decide on transaction states.

### HolidayBookingController

The physical REST endpoint. It is strictly responsible for HTTP semantics (routes, status codes, and headers).

```java
package com.example.travel.core.infrastructure.holiday.adapter.in.web;

import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingRequest;
import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday Booking", description = "Endpunkte für Ferienbuchungen")
public class HolidayBookingController {

    private final HolidayRestService restService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neue Ferien buchen (Flug + Hotel + AddOns)")
    public HolidayBookingResponse book(
            @RequestHeader("X-Customer-Id") String customerId,
            @Valid @RequestBody HolidayBookingRequest request) {

        // Direkte Delegation an den Rest Service
        return restService.book(customerId, request);
    }
}
```

### HolidayRestService

Decouples the controller from the actual use case. It orchestrates parameter parsing and mappings using MapStruct.

```java
package com.example.travel.core.infrastructure.holiday.adapter.in.web;

import com.example.travel.core.business.holiday.BookHolidayUseCase;
import com.example.travel.core.business.holiday.CancelHolidayUseCase;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingRequest;
import com.example.travel.core.infrastructure.holiday.adapter.in.web.dto.HolidayBookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayRestService {

    private final BookHolidayUseCase bookHolidayUseCase;
    private final CancelHolidayUseCase cancelHolidayUseCase;
    private final HolidayRestMapper mapper;

    /**
     * Nimmt den validierten REST-Request entgegen, bereitet die Parameter vor
     * und delegiert die Geschäftslogik an den Use Case.
     */
    public HolidayBookingResponse book(String customerId, HolidayBookingRequest request) {

        // 1. Mapping & Parameter-Aufbereitung
        String destination = request.getDestination();
        List<HolidayBooking.AddOn> addOns = request.getAddOns() != null ? request.getAddOns() : List.of();

        // 2. Aufruf der Geschäftslogik
        HolidayBooking domainResult = bookHolidayUseCase.execute(customerId, destination, addOns);

        // 3. Mapping: Domain -> Response-DTO
        return mapper.toResponse(domainResult);
    }
}
```

## 2. The Business Layer (Orchestration & Transactions)

This layer orchestrates the core workflow. It manages database transactions, loads necessary aggregates/entities via Outbound Ports, instantiates and saves aggregates, and coordinates compensating actions (Saga Pattern) when distributed transactions fail.

### BookHolidayUseCase

The transactional orchestration boundary for booking a holiday.

```java
package com.example.travel.core.business.holiday;

import com.example.travel.core.business.holiday.port.CustomerProfilePort;
import com.example.travel.core.business.holiday.port.FlightServicePort;
import com.example.travel.core.business.holiday.port.HolidayBookingPort;
import com.example.travel.core.business.holiday.port.HotelServicePort;
import com.example.travel.core.domain.customer.model.CustomerProfile;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class BookHolidayUseCase {

    private final HolidayBookingPort bookingPort;
    private final CustomerProfilePort customerPort;
    private final FlightServicePort flightPort;
    private final HotelServicePort hotelPort;
    private final HolidayNotificationService notificationService; // Injected Business Service
    private final HolidayPricingService pricingService; // Injected Business Service

    public HolidayBooking execute(String customerId, String destination, List<HolidayBooking.AddOn> addOns) {

        CustomerProfile customer = customerPort.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Kunde nicht gefunden"));

        HolidayBooking booking = new HolidayBooking();
        booking.setUid(UUID.randomUUID().toString());
        booking.setCustomerId(customerId);
        booking.setDestination(destination);
        booking.setAddOns(addOns);
        booking.setStatus(HolidayBooking.BookingStatus.PENDING);

        BigDecimal finalPrice = pricingService.calculateTotal(booking, customer);
        booking.setTotalPrice(finalPrice);

        bookingPort.save(booking);

        String flightId = null;
        String hotelId = null;

        try {
            flightId = flightPort.bookFlight(customerId, destination);
            hotelId = hotelPort.bookHotel(customerId, destination, addOns);

            booking.confirm(flightId, hotelId);
            HolidayBooking confirmedBooking = bookingPort.save(booking);

            // Trigger notification on success
            notificationService.notifyCustomerAndLog(confirmedBooking, "Ihre Ferienbuchung nach " + destination + " wurde erfolgreich bestätigt.");

            return confirmedBooking;

        } catch (Exception e) {
            log.error("Buchung fehlgeschlagen für UID: {}", booking.getUid(), e);

            if (flightId != null && hotelId == null) {
                try {
                    flightPort.cancelFlight(flightId);
                } catch (Exception cancelEx) {
                    log.error("KRITISCH: Kompensation fehlgeschlagen!", cancelEx);
                }
            }

            booking.markAsFailed();
            HolidayBooking failedBooking = bookingPort.save(booking);

            // Trigger notification on failure
            notificationService.notifyCustomerAndLog(failedBooking, "Ferienbuchung fehlgeschlagen: " + e.getMessage());

            throw new BookingFailedException("Ferienbuchung fehlgeschlagen.", e);
        }
    }
}
```

### HolidayPricingService

This service contains stateless calculation rules. Located in the business module, it has no direct Outbound Ports as it only processes memory structures.

```java
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
```

### HolidayNotificationService

A coordinating Business Service that orchestrates communication with customers and audit components via Outbound Ports.

```java
package com.example.travel.core.business.holiday;

import com.example.travel.core.business.port.AuditLogPort;
import com.example.travel.core.business.port.NotificationPort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayNotificationService {

    private final NotificationPort notificationPort;
    private final AuditLogPort auditLogPort;

    // Diese Logik ist Orchestrierung (Business), keine reine Fachlogik (Domain)
    public void notifyCustomerAndLog(HolidayBooking booking, String message) {
        notificationPort.sendSms(booking.getCustomerId(), message);

        if (booking.getStatus() == HolidayBooking.BookingStatus.FAILED) {
            auditLogPort.logCriticalFailure("Booking failed for customer: " + booking.getCustomerId());
        } else {
            auditLogPort.logSuccess("Booking successful: " + booking.getUid());
        }
    }
}
```

## 3. The Domain Layer (Inside)

The pure core of the application. It contains pure business logic and business rules. This layer has absolutely zero dependencies on Spring, JPA, databases, Web frameworks, or third-party APIs. It is highly testable using pure JUnit tests.

### HolidayBooking (Aggregate Root)

```java
package com.example.travel.core.domain.holiday.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class HolidayBooking {
    private String uid;
    private String customerId;
    private String destination;
    private BookingStatus status;
    private BigDecimal totalPrice;

    // Externe Referenzen
    private String flightConfirmationId;
    private String hotelConfirmationId;
    private List<AddOn> addOns = new ArrayList<>();

    public enum BookingStatus {PENDING, CONFIRMED, FAILED, CANCELLED}

    public enum AddOn {SPA, ALL_INCLUSIVE, LATE_CHECKOUT}

    public void confirm(String flightId, String hotelId) {
        this.flightConfirmationId = flightId;
        this.hotelConfirmationId = hotelId;
        this.status = BookingStatus.CONFIRMED;
    }

    public void markAsFailed() {
        this.status = BookingStatus.FAILED;
    }
}
```

## 4. The Outbound Ports (Interfaces)

Ports are interface contracts declared inside the Business packages. They specify what the application needs from the external world. The technical implementation (how it is resolved) is delegated entirely to the outer infrastructure layer. All contracts here strictly end with the `Port` suffix.

```java
package com.example.travel.core.business.holiday.port;

import com.example.travel.core.domain.holiday.model.HolidayBooking;
import java.util.List;
import java.util.Optional;

// Interfacing with downstream systems (Ports)
public interface FlightServicePort {
    String bookFlight(String customerId, String destination);
    void cancelFlight(String flightConfirmationId); 
}

public interface HotelServicePort {
    String bookHotel(String customerId, String destination, List<HolidayBooking.AddOn> addOns);
    void cancelHotel(String hotelConfirmationId);
}

public interface NotificationPort {
    void sendSms(String customerId, String message);
    void sendEmail(String customerId, String subject, String body);
}

public interface AuditLogPort {
    void logSuccess(String message);
    void logCriticalFailure(String message);
}

// Interfacing with persistence (Ports)
public interface HolidayBookingPort {
    HolidayBooking save(HolidayBooking booking);
    Optional<HolidayBooking> findByUid(String uid);
}

public interface CustomerProfilePort {
    Optional<CustomerProfile> findByCustomerId(String customerId);
}
```

## 5. The Outbound Infrastructure & Persistence (Outside Again)

Contains technical implementations of Outbound Ports. It leverages framework-specific utilities like Spring Data JPA repositories, REST adapters (`RestTemplate` or `WebClient`), and mapping engines (MapStruct).

### BookingRepositoryAdapter

Implements `HolidayBookingRepositoryPort`. It translates the domain model into a JPA entity, persists it via Spring Data, and translates the updated entity back to the domain model to avoid entity leakage.

```java
package com.example.travel.core.infrastructure.holiday.adapter.out.persistence;

import com.example.travel.core.business.holiday.port.HolidayBookingPort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingRepositoryAdapter implements HolidayBookingPort {

    private final BookingSpringDataRepo jpaRepo;
    private final BookingPersistenceMapper mapper;

    @Override
    public HolidayBooking save(HolidayBooking booking) {
        BookingJpaEntity entity = mapper.toJpaEntity(booking);
        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<HolidayBooking> findByUid(String bookingUid) {
        return jpaRepo.findByUid(bookingUid).map(mapper::toDomain);
    }
}
```

### BookingJpaEntity

The database representation. Contains Spring and JPA annotations, schema details, indexes, and join mappings.

```java
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
```

## Hotel Service Outbound Routing Pattern (Proxy & Implementations)

The `HotelServicePort` has a multi-provider setup. To prevent coupling the core business workflow to specific API providers, we use a Routing Proxy pattern.

Three individual implementations of the `HotelServicePort` interface co-exist within the infrastructure module:

1. **HotelServiceRoutingProxy**: Declared as `@Primary`. It acts as the routing orchestrator, intercepting calls and deciding dynamically which underlying provider to delegate the action to (e.g., using properties or tenant identifiers).

2. **BookingComAdapter**: Annotated with `@Component("bookingCom")`. Handles the technical integration with the Booking.com external API.

3. **TrivagoAdapter**: Annotated with `@Component("trivago")`. Handles the technical integration with the Trivago external API.

### HotelServiceRoutingProxy

```java
package com.example.travel.core.infrastructure.holiday.adapter.out.hotel;

import com.example.travel.core.business.holiday.port.HotelServicePort;
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
```

### BookingComAdapter

```java
package com.example.travel.core.infrastructure.holiday.adapter.out.hotel;

import com.example.travel.core.business.holiday.port.HotelServicePort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("bookingCom")
@RequiredArgsConstructor
public class BookingComAdapter implements HotelServicePort {

    @Override
    public String bookHotel(String customerId, String destination, List<HolidayBooking.AddOn> addOns) {
        return "BOOK-12493520";
    }

    @Override
    public void cancelHotel(String hotelConfirmationId) {

    }
}

```

### TrivagoAdapter

```java
package com.example.travel.core.infrastructure.holiday.adapter.out.hotel;

import com.example.travel.core.business.holiday.port.HotelServicePort;
import com.example.travel.core.domain.holiday.model.HolidayBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("trivago")
@RequiredArgsConstructor
public class TrivagoAdapter implements HotelServicePort {
    @Override
    public String bookHotel(String customerId, String destination, List<HolidayBooking.AddOn> addOns) {
        return "TRIV-23482";
    }

    @Override
    public void cancelHotel(String hotelConfirmationId) {

    }
}
```