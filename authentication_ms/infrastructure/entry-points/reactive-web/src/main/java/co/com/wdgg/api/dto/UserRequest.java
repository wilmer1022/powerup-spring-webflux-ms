package co.com.wdgg.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// User Request DTO for creating a new user
public record UserRequest(
        String documentNumber,
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        String address,
        LocalDate birthDate,
        BigDecimal salary) {

}
