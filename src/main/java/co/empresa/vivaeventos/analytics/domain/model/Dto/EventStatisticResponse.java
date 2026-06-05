package co.empresa.vivaeventos.analytics.domain.model.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class EventStatisticResponse {
    private UUID id;
    private UUID eventId;
    private String eventName;
    private OffsetDateTime eventDate;
    private Integer totalCapacity;
    private Integer ticketsSold;
    private Integer ticketsAvailable;
    private BigDecimal revenue;
    private Integer salesCount;
    private Integer cancellationCount;
    private Integer checkinCount;
    private BigDecimal occupancyPercentage;
    private String eventStatus;
    private String category;
}
