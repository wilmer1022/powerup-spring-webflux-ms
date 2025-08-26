package co.com.wdgg.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import co.com.wdgg.model.userrole.UserRole;

public record User(
        UUID id,
        String documentNumber,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        String email,
        BigDecimal salary,
        UserRole role) {
}
