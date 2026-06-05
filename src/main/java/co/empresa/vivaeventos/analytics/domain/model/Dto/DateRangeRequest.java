package co.empresa.vivaeventos.analytics.domain.model.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRangeRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
