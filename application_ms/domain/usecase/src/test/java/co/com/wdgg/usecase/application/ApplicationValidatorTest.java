package co.com.wdgg.usecase.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.usecase.application.validators.ApplicationValidator;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationValidatorTest {
    
    // Helper method to create a valid application instance
    private Application createValidApplication() {
        return new Application(
            UUID.randomUUID(),
            "user@email.com",
            new BigDecimal("5000000"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
    }

    // --- Tests for not-null and not-blank ---

    @Test
    void validateUserEmail_withNull_returnsError() {
        Application application = new Application(
            UUID.randomUUID(),
            null,
            new BigDecimal("5000000"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertEquals("El correo electrónico es obligatorio", validator.validateUserEmail());
    }

    @Test
    void validateUserEmail_withBlank_returnsError() {
        Application application = new Application(
            UUID.randomUUID(),
            "   ",
            new BigDecimal("5000000"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertEquals("El correo electrónico es obligatorio", validator.validateUserEmail());
    }

    @Test
    void validateUserEmail_withValidValue_returnsNull() {
        Application application = createValidApplication();
        ApplicationValidator validator = new ApplicationValidator(application);
        assertNull(validator.validateUserEmail());
    }

    @Test
    void validateAmount_withNull_returnsError() {
        Application application = new Application(
            UUID.randomUUID(),
            "user@email.com",
            null,
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertEquals("El monto es obligatorio", validator.validateAmount());
    }

    @Test
    void validateCreditType_withNull_returnsError() {
        Application application = new Application(
            UUID.randomUUID(),
            "user@email.com",
            new BigDecimal("5000000"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertEquals("El tipo de credito es obligatorio", validator.validateCreditType());
    }

    // --- Tests methods for format ---

    @Test
    void validateAmountForm_withValueAtLowerBound_returnsNull() {
        Application application = new Application(
            UUID.randomUUID(),
            "user@email.com",
            new BigDecimal("0"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertNull(validator.validateAmountForm());
    }

    @Test
    void validateAmountForm_withValueAtUpperBound_returnsNull() {
        Application application = new Application(
            UUID.randomUUID(),
            "user@email.com",
            new BigDecimal("10000000000"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertNull(validator.validateAmountForm());
    }

    @Test
    void validateAmountForm_withValueBelowLowerBound_returnsError() {
        Application application = new Application(
            UUID.randomUUID(),
            "user@email.com",
            new BigDecimal("-1"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertEquals("El monto debe estar entre 0 y 10000000000", validator.validateAmountForm());
    }

    @Test
    void validateAmountForm_withValueAboveUpperBound_returnsError() {
        Application application = new Application(
            UUID.randomUUID(),
            "user@email.com",
            new BigDecimal("10000000001"),
            LocalDate.of(2023, 1, 1),
            null,
            null
        );
        ApplicationValidator validator = new ApplicationValidator(application);
        assertEquals("El monto debe estar entre 0 y 10000000000", validator.validateAmountForm());
    }

    @Test
    void validateAmountForm_withValueWithinRange_returnsNull() {
        Application application = createValidApplication();
        ApplicationValidator validator = new ApplicationValidator(application);
        assertNull(validator.validateAmountForm());
    }
}
