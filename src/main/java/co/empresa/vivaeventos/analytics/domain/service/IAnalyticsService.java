package co.empresa.vivaeventos.analytics.domain.service;

import co.empresa.vivaeventos.analytics.domain.model.Dto.DashboardResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.EventStatisticResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.SalesSummaryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IAnalyticsService {

    DashboardResponse getDashboard();

    EventStatisticResponse getEventStatistics(UUID eventId);

    List<EventStatisticResponse> getAllEventStatistics();

    List<SalesSummaryResponse> getSalesSummary(LocalDate startDate, LocalDate endDate);

    List<SalesSummaryResponse> getEventSalesSummary(UUID eventId, LocalDate startDate, LocalDate endDate);

    EventStatisticResponse updateEventStatistic(UUID eventId, String eventName, String category,
                                                 Integer ticketsSold, BigDecimal revenue,
                                                 Integer totalCapacity, String eventStatus);

    void recordDailySale(UUID eventId, LocalDate date, Integer ticketsSold, BigDecimal revenue, Integer orderCount);
}
