package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ApplicationReviewableResponse(
        UUID id,
        String userEmail,
        String firstName,
        String lastName,
        BigDecimal amount,
        LocalDate creditPeriod,
        String creditType,
        BigDecimal interestRate,
        String status,
        BigDecimal salary,
        BigDecimal monthlyRequestAmount) {

}