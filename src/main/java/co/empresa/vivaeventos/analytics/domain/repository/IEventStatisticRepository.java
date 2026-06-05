package co.empresa.vivaeventos.analytics.domain.repository;

import co.empresa.vivaeventos.analytics.domain.model.EventStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEventStatisticRepository extends JpaRepository<EventStatistic, UUID> {

    Optional<EventStatistic> findByEventId(UUID eventId);

    List<EventStatistic> findAllByOrderByRevenueDesc();

    List<EventStatistic> findAllByOrderByTicketsSoldDesc();

    @Query("SELECT COALESCE(SUM(e.ticketsSold), 0) FROM EventStatistic e")
    Integer getTotalTicketsSold();

    @Query("SELECT COALESCE(SUM(e.totalCapacity), 0) FROM EventStatistic e")
    Integer getTotalCapacity();

    @Query("SELECT COALESCE(SUM(e.revenue), 0) FROM EventStatistic e")
    java.math.BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(e.checkinCount), 0) FROM EventStatistic e")
    Integer getTotalCheckins();

    @Query("SELECT COALESCE(SUM(e.salesCount), 0) FROM EventStatistic e")
    Integer getTotalSalesCount();
}
