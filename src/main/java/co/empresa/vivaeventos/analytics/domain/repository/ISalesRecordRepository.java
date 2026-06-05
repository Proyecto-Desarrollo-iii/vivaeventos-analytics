package co.empresa.vivaeventos.analytics.domain.repository;

import co.empresa.vivaeventos.analytics.domain.model.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ISalesRecordRepository extends JpaRepository<SalesRecord, UUID> {

    List<SalesRecord> findByRecordDateBetweenOrderByRecordDateAsc(LocalDate start, LocalDate end);

    List<SalesRecord> findByEventIdAndRecordDateBetweenOrderByRecordDateAsc(UUID eventId, LocalDate start, LocalDate end);

    List<SalesRecord> findTop10ByOrderByRecordDateDesc();
}
