package co.empresa.vivaeventos.analytics.delivery.rest;

import co.empresa.vivaeventos.analytics.domain.model.Dto.DashboardResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.EventStatisticResponse;
import co.empresa.vivaeventos.analytics.domain.model.Dto.SalesSummaryResponse;
import co.empresa.vivaeventos.analytics.domain.service.IAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final IAnalyticsService analyticsService;

    public AnalyticsController(IAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        try {
            DashboardResponse dashboard = analyticsService.getDashboard();
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Dashboard obtenido exitosamente");
            response.put("dashboard", dashboard);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> getAllEventStatistics() {
        try {
            List<EventStatisticResponse> stats = analyticsService.getAllEventStatistics();
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Estadisticas obtenidas exitosamente");
            response.put("estadisticas", stats);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Map<String, Object>> getEventStatistics(@PathVariable UUID eventId) {
        try {
            EventStatisticResponse stats = analyticsService.getEventStatistics(eventId);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Estadisticas del evento obtenidas exitosamente");
            response.put("estadisticas", stats);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<SalesSummaryResponse> sales = analyticsService.getSalesSummary(startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Resumen de ventas obtenido exitosamente");
            response.put("ventas", sales);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/sales/events/{eventId}")
    public ResponseEntity<Map<String, Object>> getEventSalesSummary(
            @PathVariable UUID eventId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<SalesSummaryResponse> sales = analyticsService.getEventSalesSummary(eventId, startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Resumen de ventas del evento obtenido exitosamente");
            response.put("ventas", sales);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
