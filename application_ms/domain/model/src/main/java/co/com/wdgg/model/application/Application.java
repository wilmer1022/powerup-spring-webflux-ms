package co.com.wdgg.model.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record Application(
        UUID id,
        String userDocumentNumber,
        BigDecimal amount,
        LocalDate creditPeriod,
        String creditType,
        String creditStatus) {
}
