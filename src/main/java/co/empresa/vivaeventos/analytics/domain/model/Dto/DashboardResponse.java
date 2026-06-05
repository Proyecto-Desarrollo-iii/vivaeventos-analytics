package co.empresa.vivaeventos.analytics.domain.model.Dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponse {
    private Integer totalEvents;
    private Integer activeEvents;
    private Integer totalTicketsSold;
    private Integer totalCapacity;
    private BigDecimal totalRevenue;
    private BigDecimal averageOccupancy;
    private Integer totalOrders;
    private Integer totalCheckins;
    private List<EventStatisticResponse> topEvents;
    private List<SalesSummaryResponse> recentSales;
}
