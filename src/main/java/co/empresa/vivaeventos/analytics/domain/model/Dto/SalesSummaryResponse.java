package co.empresa.vivaeventos.analytics.domain.model.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class SalesSummaryResponse {
    private UUID id;
    private UUID eventId;
    private LocalDate recordDate;
    private Integer ticketsSold;
    private BigDecimal revenue;
    private Integer orderCount;
}
