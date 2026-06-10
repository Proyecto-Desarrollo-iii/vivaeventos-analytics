package co.empresa.vivaeventos.analytics.delivery.rest;

import co.empresa.vivaeventos.analytics.domain.model.Dto.DashboardResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.EventStatisticResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.SalesSummaryResponse;
import co.empresa.vivaeventos.analytics.domain.service.IAnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AnalyticsController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAnalyticsService analyticsService;

    @Test
    void getDashboard_ShouldReturn200() throws Exception {
        DashboardResponse dashboard = new DashboardResponse();
        dashboard.setTotalEvents(5);
        dashboard.setTotalRevenue(BigDecimal.valueOf(100000));

        when(analyticsService.getDashboard()).thenReturn(dashboard);

        mockMvc.perform(get("/api/v1/analytics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dashboard.totalEvents").value(5));
    }

    @Test
    void getDashboard_WhenServiceThrows_ShouldReturn400() throws Exception {
        when(analyticsService.getDashboard()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/v1/analytics/dashboard"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("DB error"));
    }

    @Test
    void getAllEventStatistics_ShouldReturn200() throws Exception {
        EventStatisticResponse stat = new EventStatisticResponse();
        stat.setEventName("Test");
        stat.setTicketsSold(50);

        when(analyticsService.getAllEventStatistics()).thenReturn(List.of(stat));

        mockMvc.perform(get("/api/v1/analytics/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadisticas[0].eventName").value("Test"));
    }

    @Test
    void getAllEventStatistics_WhenServiceThrows_ShouldReturn400() throws Exception {
        when(analyticsService.getAllEventStatistics()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/analytics/events"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error"));
    }

    @Test
    void getEventStatistics_ShouldReturn200() throws Exception {
        UUID eventId = UUID.randomUUID();
        EventStatisticResponse stat = new EventStatisticResponse();
        stat.setEventId(eventId);
        stat.setEventName("My Event");

        when(analyticsService.getEventStatistics(eventId)).thenReturn(stat);

        mockMvc.perform(get("/api/v1/analytics/events/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadisticas.eventName").value("My Event"));
    }

    @Test
    void getEventStatistics_WhenServiceThrows_ShouldReturn400() throws Exception {
        when(analyticsService.getEventStatistics(any(UUID.class))).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/v1/analytics/events/{eventId}", UUID.randomUUID()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void getSalesSummary_ShouldReturn200() throws Exception {
        SalesSummaryResponse sale = new SalesSummaryResponse();
        sale.setRevenue(BigDecimal.valueOf(5000));
        sale.setTicketsSold(50);

        when(analyticsService.getSalesSummary(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(sale));

        mockMvc.perform(get("/api/v1/analytics/sales")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventas[0].revenue").value(5000));
    }

    @Test
    void getSalesSummary_WhenServiceThrows_ShouldReturn400() throws Exception {
        when(analyticsService.getSalesSummary(any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("Sales error"));

        mockMvc.perform(get("/api/v1/analytics/sales")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Sales error"));
    }

    @Test
    void getEventSalesSummary_ShouldReturn200() throws Exception {
        UUID eventId = UUID.randomUUID();
        SalesSummaryResponse sale = new SalesSummaryResponse();
        sale.setEventId(eventId);
        sale.setRevenue(BigDecimal.valueOf(3000));

        when(analyticsService.getEventSalesSummary(eq(eventId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(sale));

        mockMvc.perform(get("/api/v1/analytics/sales/events/{eventId}", eventId)
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventas[0].revenue").value(3000));
    }

    @Test
    void getEventSalesSummary_WhenServiceThrows_ShouldReturn400() throws Exception {
        when(analyticsService.getEventSalesSummary(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("Event sales error"));

        mockMvc.perform(get("/api/v1/analytics/sales/events/{eventId}", UUID.randomUUID())
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Event sales error"));
    }
}
