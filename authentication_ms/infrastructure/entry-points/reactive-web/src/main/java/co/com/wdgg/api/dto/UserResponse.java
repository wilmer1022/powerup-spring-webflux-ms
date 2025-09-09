package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String documentNumber,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String address,
        LocalDate birthDate,
        BigDecimal salary,
        String role) {

}
