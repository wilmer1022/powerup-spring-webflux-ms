package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationResponse(
        @Schema(
                description = "Identificador único de la solicitud",
                example = "a6193a92-6274-4c24-8c11-49075ed5118d"
        )
        UUID id,
        @Schema(
                description = "Número de documento del usuario",
                example = "1199999999"
        )
        String userDocumentNumber,
        @Schema(
                description = "Monto de la solicitud",
                example = "4500000",
                format = "double",
                minLength = 0
        )
        BigDecimal amount,
        @Schema(
                description = "Fecha de finalización del plazo de crédito",
                example = "2030-12-31",
                format = "date"
        )
        LocalDate creditPeriod,
        @Schema(
                description = "Descripción del tipo de crédito",
                example = "PERSONAL"
        )
        String creditType,
        @Schema(
                description = "Tasa de interés",
                example = "0.1",
                format = "double",
                minLength = 0
        )
        BigDecimal interestRate,
        @Schema(
                description = "Descripción del estado de la solicitud",
                example = "PENDIENTE"
        )
        String status) {

}
