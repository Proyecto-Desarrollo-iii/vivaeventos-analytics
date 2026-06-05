package co.empresa.vivaeventos.analytics.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_statistics")
@Getter
@Setter
public class EventStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_date")
    private OffsetDateTime eventDate;

    @Column(name = "total_capacity")
    private Integer totalCapacity = 0;

    @Column(name = "tickets_sold")
    private Integer ticketsSold = 0;

    @Column(name = "tickets_available")
    private Integer ticketsAvailable = 0;

    @Column(name = "revenue", precision = 15, scale = 2)
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(name = "sales_count")
    private Integer salesCount = 0;

    @Column(name = "cancellation_count")
    private Integer cancellationCount = 0;

    @Column(name = "checkin_count")
    private Integer checkinCount = 0;

    @Column(name = "occupancy_percentage", precision = 5, scale = 2)
    private BigDecimal occupancyPercentage = BigDecimal.ZERO;

    @Column(name = "event_status", length = 50)
    private String eventStatus;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        lastUpdated = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = OffsetDateTime.now();
    }
}
