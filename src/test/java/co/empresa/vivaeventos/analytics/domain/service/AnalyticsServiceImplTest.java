package co.empresa.vivaeventos.analytics.domain.service;

import co.empresa.vivaeventos.analytics.domain.model.Dto.DashboardResponse;
import co.empresa.vivaeventos.analytics.domain.model.EventStatistic;
import co.empresa.vivaeventos.analytics.domain.repository.IEventStatisticRepository;
import co.empresa.vivaeventos.analytics.domain.repository.ISalesRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.empresa.vivaeventos.analytics.domain.model.SalesRecord;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private IEventStatisticRepository eventStatisticRepository;

    @Mock
    private ISalesRecordRepository salesRecordRepository;

    private AnalyticsServiceImpl analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsServiceImpl(eventStatisticRepository, salesRecordRepository);
    }

    @Test
    void getDashboard_ShouldReturnEmptyDashboard_WhenNoData() {
        when(eventStatisticRepository.findAll()).thenReturn(Collections.emptyList());
        when(eventStatisticRepository.getTotalTicketsSold()).thenReturn(0);
        when(eventStatisticRepository.getTotalCapacity()).thenReturn(0);
        when(eventStatisticRepository.getTotalRevenue()).thenReturn(BigDecimal.ZERO);
        when(eventStatisticRepository.getTotalCheckins()).thenReturn(0);
        when(eventStatisticRepository.getTotalSalesCount()).thenReturn(0);

        DashboardResponse response = analyticsService.getDashboard();

        assertNotNull(response);
        assertEquals(0, response.getTotalEvents());
        assertEquals(BigDecimal.ZERO, response.getTotalRevenue());
    }

    @Test
    void getEventStatistics_ShouldThrowException_WhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        when(eventStatisticRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> analyticsService.getEventStatistics(eventId));
    }

    @Test
    void getEventStatistics_ShouldReturnStats_WhenEventExists() {
        UUID eventId = UUID.randomUUID();
        EventStatistic stat = new EventStatistic();
        stat.setEventId(eventId);
        stat.setEventName("Test Event");
        stat.setTicketsSold(100);
        stat.setRevenue(BigDecimal.valueOf(5000));

        when(eventStatisticRepository.findByEventId(eventId)).thenReturn(Optional.of(stat));

        var response = analyticsService.getEventStatistics(eventId);

        assertNotNull(response);
        assertEquals("Test Event", response.getEventName());
        assertEquals(100, response.getTicketsSold());
    }

    @Test
    void updateEventStatistic_ShouldCreateNew_WhenNotExists() {
        UUID eventId = UUID.randomUUID();
        when(eventStatisticRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        EventStatistic savedStat = new EventStatistic();
        savedStat.setEventId(eventId);
        savedStat.setEventName("New Event");
        savedStat.setTotalCapacity(500);
        savedStat.setTicketsSold(100);
        savedStat.setRevenue(BigDecimal.valueOf(2500));
        savedStat.setOccupancyPercentage(BigDecimal.valueOf(20.00));

        when(eventStatisticRepository.save(any(EventStatistic.class))).thenReturn(savedStat);

        var response = analyticsService.updateEventStatistic(
                eventId, "New Event", "Concierto",
                100, BigDecimal.valueOf(2500), 500, "PUBLISHED"
        );

        assertNotNull(response);
        assertEquals("New Event", response.getEventName());
    }

    @Test
    void updateEventStatistic_ShouldUpdateExisting_WhenExists() {
        UUID eventId = UUID.randomUUID();
        EventStatistic existing = new EventStatistic();
        existing.setEventId(eventId);
        existing.setEventName("Old Event");
        existing.setTotalCapacity(1000);
        existing.setTicketsSold(200);
        existing.setRevenue(BigDecimal.valueOf(5000));
        existing.setTicketsAvailable(800);
        existing.setOccupancyPercentage(BigDecimal.valueOf(20.00));

        when(eventStatisticRepository.findByEventId(eventId)).thenReturn(Optional.of(existing));

        EventStatistic updated = new EventStatistic();
        updated.setEventId(eventId);
        updated.setEventName("Updated Event");
        updated.setTotalCapacity(1000);
        updated.setTicketsSold(300);
        updated.setRevenue(BigDecimal.valueOf(7500));
        updated.setTicketsAvailable(700);
        updated.setOccupancyPercentage(BigDecimal.valueOf(30.00));

        when(eventStatisticRepository.save(any(EventStatistic.class))).thenReturn(updated);

        var response = analyticsService.updateEventStatistic(
                eventId, "Updated Event", null,
                300, BigDecimal.valueOf(7500), null, null
        );

        assertNotNull(response);
        assertEquals("Updated Event", response.getEventName());
        assertEquals(300, response.getTicketsSold());
    }

    @Test
    void getDashboard_ShouldReturnDashboardWithData() {
        UUID eventId = UUID.randomUUID();
        EventStatistic stat = new EventStatistic();
        stat.setEventId(eventId);
        stat.setEventName("Top Event");
        stat.setTicketsSold(100);
        stat.setTotalCapacity(500);
        stat.setRevenue(BigDecimal.valueOf(10000));
        stat.setEventStatus("PUBLISHED");
        stat.setTicketsAvailable(400);
        stat.setOccupancyPercentage(BigDecimal.valueOf(20.00));

        when(eventStatisticRepository.findAll()).thenReturn(List.of(stat));
        when(eventStatisticRepository.getTotalTicketsSold()).thenReturn(100);
        when(eventStatisticRepository.getTotalCapacity()).thenReturn(500);
        when(eventStatisticRepository.getTotalRevenue()).thenReturn(BigDecimal.valueOf(10000));
        when(eventStatisticRepository.getTotalCheckins()).thenReturn(50);
        when(eventStatisticRepository.getTotalSalesCount()).thenReturn(30);

        SalesRecord record = new SalesRecord();
        record.setEventId(eventId);
        record.setRevenue(BigDecimal.valueOf(500));
        record.setTicketsSold(5);
        record.setOrderCount(1);

        when(salesRecordRepository.findTop10ByOrderByRecordDateDesc()).thenReturn(List.of(record));

        DashboardResponse response = analyticsService.getDashboard();

        assertNotNull(response);
        assertEquals(1, response.getTotalEvents());
        assertEquals(1, response.getActiveEvents());
        assertEquals(BigDecimal.valueOf(10000), response.getTotalRevenue());
        assertEquals(100, response.getTotalTicketsSold());
        assertEquals(500, response.getTotalCapacity());
        assertEquals(30, response.getTotalOrders());
        assertEquals(50, response.getTotalCheckins());
        assertEquals(0, BigDecimal.valueOf(20.00).compareTo(response.getAverageOccupancy()));
        assertEquals(1, response.getTopEvents().size());
        assertEquals(1, response.getRecentSales().size());
    }

    @Test
    void getDashboard_WhenNoCapacity_ShouldSetZeroOccupancy() {
        when(eventStatisticRepository.findAll()).thenReturn(Collections.emptyList());
        when(eventStatisticRepository.getTotalTicketsSold()).thenReturn(0);
        when(eventStatisticRepository.getTotalCapacity()).thenReturn(0);
        when(eventStatisticRepository.getTotalRevenue()).thenReturn(BigDecimal.ZERO);
        when(eventStatisticRepository.getTotalCheckins()).thenReturn(0);
        when(eventStatisticRepository.getTotalSalesCount()).thenReturn(0);
        when(salesRecordRepository.findTop10ByOrderByRecordDateDesc()).thenReturn(Collections.emptyList());

        DashboardResponse response = analyticsService.getDashboard();

        assertEquals(BigDecimal.ZERO, response.getAverageOccupancy());
        assertEquals(0, response.getTopEvents().size());
        assertEquals(0, response.getRecentSales().size());
    }

    @Test
    void getAllEventStatistics_ShouldReturnList() {
        UUID eventId = UUID.randomUUID();
        EventStatistic stat = new EventStatistic();
        stat.setEventId(eventId);
        stat.setEventName("Event 1");
        stat.setRevenue(BigDecimal.valueOf(5000));

        when(eventStatisticRepository.findAllByOrderByRevenueDesc()).thenReturn(List.of(stat));

        var response = analyticsService.getAllEventStatistics();

        assertEquals(1, response.size());
        assertEquals("Event 1", response.get(0).getEventName());
    }

    @Test
    void getSalesSummary_ShouldReturnList() {
        SalesRecord record = new SalesRecord();
        record.setEventId(UUID.randomUUID());
        record.setRevenue(BigDecimal.valueOf(1000));
        record.setTicketsSold(10);

        when(salesRecordRepository.findByRecordDateBetweenOrderByRecordDateAsc(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(record));

        var response = analyticsService.getSalesSummary(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

        assertEquals(1, response.size());
        assertEquals(BigDecimal.valueOf(1000), response.get(0).getRevenue());
    }

    @Test
    void getEventSalesSummary_ShouldReturnList() {
        UUID eventId = UUID.randomUUID();
        SalesRecord record = new SalesRecord();
        record.setEventId(eventId);
        record.setRevenue(BigDecimal.valueOf(2000));

        when(salesRecordRepository.findByEventIdAndRecordDateBetweenOrderByRecordDateAsc(
                eq(eventId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(record));

        var response = analyticsService.getEventSalesSummary(
                eventId, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

        assertEquals(1, response.size());
        assertEquals(BigDecimal.valueOf(2000), response.get(0).getRevenue());
    }

    @Test
    void recordDailySale_ShouldSaveRecord() {
        UUID eventId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        analyticsService.recordDailySale(eventId, date, 50, BigDecimal.valueOf(2500), 10);

        verify(salesRecordRepository).save(any(SalesRecord.class));
    }

}
