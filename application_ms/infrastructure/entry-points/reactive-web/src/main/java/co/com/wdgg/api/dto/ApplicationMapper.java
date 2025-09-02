package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationstatus.ApplicationStatus;

@Component
public class ApplicationMapper {
    public Application toApplication(ApplicationRequest request) {
        return new Application(
                null,
                request.userEmail(),
                request.amount(),
                request.creditPeriod(),
                null,
                new ApplicationCreditType(
                        UUID.fromString(request.creditType()),
                        null,
                        null,
                        null,
                        BigDecimal.ZERO,
                        false));
    }

    public ApplicationResponse toResponse(Application application) {
        String creditType = Optional.ofNullable(application.applicationCreditType())
                .map(ApplicationCreditType::creditType)
                .orElse(null);
        BigDecimal interestRate = Optional.ofNullable(application.applicationCreditType())
                .map(ApplicationCreditType::interestRate)
                .orElse(null);
        String statusDescription = Optional.ofNullable(application.applicationStatus())
                .map(ApplicationStatus::description)
                .orElse(null);
        return new ApplicationResponse(
                application.id(),
                application.userEmail(),
                application.amount(),
                application.creditPeriod(),
                creditType,
                interestRate,
                statusDescription);
    }
}