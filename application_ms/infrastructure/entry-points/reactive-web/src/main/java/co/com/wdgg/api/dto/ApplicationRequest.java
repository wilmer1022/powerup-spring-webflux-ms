package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApplicationRequest(
        String userEmail,
        BigDecimal amount,
        LocalDate creditPeriod,
        String creditType) {

}
