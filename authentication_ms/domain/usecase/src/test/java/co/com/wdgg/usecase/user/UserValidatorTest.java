package co.com.wdgg.usecase.user;

import org.junit.jupiter.api.Test;

import co.com.wdgg.model.user.User;
import co.com.wdgg.usecase.user.validators.UserValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserValidatorTest {

    // Helper method to create a valid user instance
    private User createValidUser() {
        return new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com", new BigDecimal("5000000"),
                null);
    }

    // --- Tests methods for not-null and not-blank ---

    @Test
    void validateDocumentNumber_withNull_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                null,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El documento es obligatorio", validator.validateDocumentNumber());
    }

    @Test
    void validateDocumentNumber_withBlank_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "   ",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El documento es obligatorio", validator.validateDocumentNumber());
    }

    @Test
    void validateDocumentNumber_withValidValue_returnsNull() {
        User user = createValidUser();
        UserValidator validator = new UserValidator(user);
        assertNull(validator.validateDocumentNumber());
    }

    @Test
    void validateFirstName_withNull_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                null,
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El nombre es obligatorio", validator.validateFirstName());
    }

    @Test
    void validateFirstName_withBlank_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "   ",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El nombre es obligatorio", validator.validateFirstName());
    }

    @Test
    void validateFirstName_withValidValue_returnsNull() {
        User user = createValidUser();
        UserValidator validator = new UserValidator(user);
        assertNull(validator.validateFirstName());
    }

    @Test
    void validateLastName_withNull_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                null,
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El apellido es obligatorio", validator.validateLastName());
    }

    @Test
    void validateLastName_withBlank_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "   ",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El apellido es obligatorio", validator.validateLastName());
    }

    @Test
    void validateLastName_withValidValue_returnsNull() {
        User user = createValidUser();
        UserValidator validator = new UserValidator(user);
        assertNull(validator.validateLastName());
    }

    @Test
    void validateEmail_withNull_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                null,
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El correo electrónico es obligatorio", validator.validateEmail());
    }

    @Test
    void validateEmail_withBlank_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "   ",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El correo electrónico es obligatorio", validator.validateEmail());
    }

    // --- Tests methods for format ---

    @Test
    void validateEmailForm_withInvalidEmail_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "invalid-email-format",
                new BigDecimal("5000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertNotNull(validator.validateEmailForm());
        assertEquals("El correo electrónico no es válido", validator.validateEmailForm());
    }

    @Test
    void validateEmailForm_withValidEmail_returnsNull() {
        User user = createValidUser();
        UserValidator validator = new UserValidator(user);
        assertNull(validator.validateEmailForm());
    }

    // --- Tests methods for cases of frontier of salary ---

    @Test
    void validateSalaryForm_withValueAtLowerBound_returnsNull() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("0"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertNull(validator.validateSalaryForm());
    }

    @Test
    void validateSalaryForm_withValueAtUpperBound_returnsNull() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("15000000"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertNull(validator.validateSalaryForm());
    }

    @Test
    void validateSalaryForm_withValueBelowLowerBound_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("-1"),
                null
        );
        UserValidator validator = new UserValidator(user);
        assertEquals("El salario debe estar entre 0 y 15000000", validator.validateSalaryForm());
    }

    @Test
    void validateSalaryForm_withValueAboveUpperBound_returnsError() {
        User user = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("15000001"),
                null
        );
        UserValidator validator = new UserValidator(user);

        assertEquals("El salario debe estar entre 0 y 15000000", validator.validateSalaryForm());
    }

    @Test
    void validateSalaryForm_withValueWithinRange_returnsNull() {
        User user = createValidUser();
        UserValidator validator = new UserValidator(user);
        assertNull(validator.validateSalaryForm());
    }
}