package co.com.wdgg.model.applicationcredittype;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplicationCreditType(
        UUID id,
        String creditType,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal interestRate,
        boolean automaticReview) {
}
