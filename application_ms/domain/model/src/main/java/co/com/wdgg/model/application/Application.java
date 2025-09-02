package co.com.wdgg.model.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationstatus.ApplicationStatus;

public record Application(
        UUID id,
        String userEmail,
        BigDecimal amount,
        LocalDate creditPeriod,
        ApplicationStatus applicationStatus,
        ApplicationCreditType applicationCreditType) {
}
