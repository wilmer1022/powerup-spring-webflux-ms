package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        String userDocumentNumber,
        BigDecimal amount,
        LocalDate creditPeriod,
        String creditType,
        BigDecimal interestRate,
        String status) {

}
