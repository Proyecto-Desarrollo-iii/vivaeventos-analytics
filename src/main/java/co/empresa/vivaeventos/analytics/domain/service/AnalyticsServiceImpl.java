package co.empresa.vivaeventos.analytics.domain.service;

import co.empresa.vivaeventos.analytics.domain.model.Dto.DashboardResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.EventStatisticResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.SalesSummaryResponse;
import co.empresa.vivaeventos.analytics.domain.model.EventStatistic;
import co.empresa.vivaeventos.analytics.domain.model.SalesRecord;
import co.empresa.vivaeventos.analytics.domain.repository.IEventStatisticRepository;
import co.empresa.vivaeventos.analytics.domain.repository.ISalesRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements IAnalyticsService {

    private final IEventStatisticRepository eventStatisticRepository;
    private final ISalesRecordRepository salesRecordRepository;

    public AnalyticsServiceImpl(IEventStatisticRepository eventStatisticRepository,
                                ISalesRecordRepository salesRecordRepository) {
        this.eventStatisticRepository = eventStatisticRepository;
        this.salesRecordRepository = salesRecordRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        DashboardResponse response = new DashboardResponse();

        List<EventStatistic> allStats = eventStatisticRepository.findAll();

        response.setTotalEvents(allStats.size());
        response.setActiveEvents((int) allStats.stream().filter(s -> "PUBLISHED".equals(s.getEventStatus())).count());
        response.setTotalTicketsSold(eventStatisticRepository.getTotalTicketsSold());
        response.setTotalCapacity(eventStatisticRepository.getTotalCapacity());
        response.setTotalRevenue(eventStatisticRepository.getTotalRevenue());
        response.setTotalOrders(eventStatisticRepository.getTotalSalesCount());
        response.setTotalCheckins(eventStatisticRepository.getTotalCheckins());

        Integer totalCap = response.getTotalCapacity();
        if (totalCap > 0) {
            BigDecimal avg = BigDecimal.valueOf(response.getTotalTicketsSold())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCap), 2, RoundingMode.HALF_UP);
            response.setAverageOccupancy(avg);
        } else {
            response.setAverageOccupancy(BigDecimal.ZERO);
        }

        response.setTopEvents(allStats.stream()
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
                .limit(5)
                .map(this::mapToStatisticResponse)
                .collect(Collectors.toList()));

        response.setRecentSales(salesRecordRepository.findTop10ByOrderByRecordDateDesc().stream()
                .map(this::mapToSalesResponse)
                .collect(Collectors.toList()));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public EventStatisticResponse getEventStatistics(UUID eventId) {
        EventStatistic stat = eventStatisticRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("No se encontraron estadisticas para el evento: " + eventId));
        return mapToStatisticResponse(stat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventStatisticResponse> getAllEventStatistics() {
        return eventStatisticRepository.findAllByOrderByRevenueDesc().stream()
                .map(this::mapToStatisticResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesSummaryResponse> getSalesSummary(LocalDate startDate, LocalDate endDate) {
        return salesRecordRepository.findByRecordDateBetweenOrderByRecordDateAsc(startDate, endDate).stream()
                .map(this::mapToSalesResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesSummaryResponse> getEventSalesSummary(UUID eventId, LocalDate startDate, LocalDate endDate) {
        return salesRecordRepository.findByEventIdAndRecordDateBetweenOrderByRecordDateAsc(eventId, startDate, endDate)
                .stream()
                .map(this::mapToSalesResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventStatisticResponse updateEventStatistic(UUID eventId, String eventName, String category,
                                                        Integer ticketsSold, BigDecimal revenue,
                                                        Integer totalCapacity, String eventStatus) {
        EventStatistic stat = eventStatisticRepository.findByEventId(eventId)
                .orElseGet(() -> {
                    EventStatistic newStat = new EventStatistic();
                    newStat.setEventId(eventId);
                    return newStat;
                });

        if (eventName != null) stat.setEventName(eventName);
        if (category != null) stat.setCategory(category);
        if (eventStatus != null) stat.setEventStatus(eventStatus);
        if (totalCapacity != null) {
            stat.setTotalCapacity(totalCapacity);
            stat.setTicketsAvailable(totalCapacity - (ticketsSold != null ? ticketsSold : stat.getTicketsSold()));
        }
        if (ticketsSold != null) {
            stat.setTicketsSold(ticketsSold);
            stat.setTicketsAvailable(stat.getTotalCapacity() - ticketsSold);
        }
        if (revenue != null) stat.setRevenue(revenue);

        if (stat.getTotalCapacity() > 0) {
            BigDecimal occupancy = BigDecimal.valueOf(stat.getTicketsSold())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(stat.getTotalCapacity()), 2, RoundingMode.HALF_UP);
            stat.setOccupancyPercentage(occupancy);
        }

        EventStatistic saved = eventStatisticRepository.save(stat);
        return mapToStatisticResponse(saved);
    }

    @Override
    @Transactional
    public void recordDailySale(UUID eventId, LocalDate date, Integer ticketsSold,
                                 BigDecimal revenue, Integer orderCount) {
        SalesRecord record = new SalesRecord();
        record.setEventId(eventId);
        record.setRecordDate(date);
        record.setTicketsSold(ticketsSold);
        record.setRevenue(revenue);
        record.setOrderCount(orderCount);
        salesRecordRepository.save(record);
    }

    private EventStatisticResponse mapToStatisticResponse(EventStatistic stat) {
        EventStatisticResponse response = new EventStatisticResponse();
        response.setId(stat.getId());
        response.setEventId(stat.getEventId());
        response.setEventName(stat.getEventName());
        response.setEventDate(stat.getEventDate());
        response.setTotalCapacity(stat.getTotalCapacity());
        response.setTicketsSold(stat.getTicketsSold());
        response.setTicketsAvailable(stat.getTicketsAvailable());
        response.setRevenue(stat.getRevenue());
        response.setSalesCount(stat.getSalesCount());
        response.setCancellationCount(stat.getCancellationCount());
        response.setCheckinCount(stat.getCheckinCount());
        response.setOccupancyPercentage(stat.getOccupancyPercentage());
        response.setEventStatus(stat.getEventStatus());
        response.setCategory(stat.getCategory());
        return response;
    }

    private SalesSummaryResponse mapToSalesResponse(SalesRecord record) {
        SalesSummaryResponse response = new SalesSummaryResponse();
        response.setId(record.getId());
        response.setEventId(record.getEventId());
        response.setRecordDate(record.getRecordDate());
        response.setTicketsSold(record.getTicketsSold());
        response.setRevenue(record.getRevenue());
        response.setOrderCount(record.getOrderCount());
        return response;
    }
}
