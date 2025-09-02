package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationRequest(
        @Schema(
                description = "Correo electrónico del usuario que realiza la solicitud",
                example = "john.doe@email.com",
                format = "email"
        )
        String userEmail,
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
                description = "Identificador único del tipo de crédito",
                example = "a6193a92-6274-4c24-8c11-49075ed5118d"
        )
        String creditType) {

}
