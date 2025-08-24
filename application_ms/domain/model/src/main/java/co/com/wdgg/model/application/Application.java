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

    public enum CreditType {
        PERSONAL,
        HIPOTECARIO,
        LIBRANZA,
        VEHICULO
    }

    public enum CreditStatus {
        PENDIENTE_DE_REVISION,
        PRE_APROBADO,
        APROBADO,
        CANCELADO,
        CERRADO
    }
}
