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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
}
