package co.com.wdgg.usecase.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.wdgg.model.user.User;
import co.com.wdgg.model.user.gateways.UserRepository;
import co.com.wdgg.model.userrole.UserRole;
import co.com.wdgg.usecase.userrole.UserRoleUseCase;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleUseCase userRoleUseCase;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        userRole = new UserRole(UUID.randomUUID(), "USER0", "USER");
        validUser = new User(
                UUID.randomUUID(),
                "123456789",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                new BigDecimal("5000000"),
                userRole
        );
    }

    // --- Tests for the createUser method ---

    @Test
    void createUser_ValidUser_ReturnsCreatedUser() {
        User userWithRole = validUser;
        when(userRepository.getUserByEmail(validUser.email()))
                .thenReturn(Mono.empty());
        when(userRoleUseCase.getUserRoleByRole("USER"))
                .thenReturn(Mono.just(userRole));
        when(userRepository.createUser(any(User.class)))
                .thenReturn(Mono.just(userWithRole));

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectNextMatches(user ->
                        user.documentNumber().equals(validUser.documentNumber()) &&
                        user.email().equals(validUser.email()) &&
                        user.role().equals(userRole))
                .verifyComplete();
    }
    
    @Test
    void createUser_EmailAlreadyExists_ThrowsIllegalArgumentException() {
        User existingUser = validUser;
        when(userRepository.getUserByEmail(validUser.email()))
                .thenReturn(Mono.just(existingUser));

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMessage("El correo electrónico " + validUser.email() + " ya está registrado")
                .verify();
    }

    @Test
    void createUser_DefaultRoleNotFound_ThrowsIllegalStateException() {
        when(userRepository.getUserByEmail(validUser.email()))
                .thenReturn(Mono.empty());
        when(userRoleUseCase.getUserRoleByRole("USER"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMessage("El rol por defecto 'USER' no existe")
                .verify();
    }

    // --- Tests for validation of data ---

    @Test
    void createUser_InvalidEmailFormat_ThrowsIllegalArgumentException() {
        User userWithInvalidEmail = new User(
                validUser.id(),
                validUser.documentNumber(),
                validUser.firstName(),
                validUser.lastName(),
                validUser.birthDate(),
                validUser.address(),
                validUser.phoneNumber(),
                "invalid-email",
                validUser.salary(),
                validUser.role()
        );
        String expectedErrorMessage = "El correo electrónico no es válido";

        StepVerifier.create(userUseCase.createUser(userWithInvalidEmail))
                .expectErrorMessage("Errores en el formulario de registro: " + expectedErrorMessage)
                .verify();
    }
    
    @Test
    void createUser_SalaryBelowLowerBound_ThrowsIllegalArgumentException() {
        User userWithInvalidSalary = new User(
                validUser.id(),
                validUser.documentNumber(),
                validUser.firstName(),
                validUser.lastName(),
                validUser.birthDate(),
                validUser.address(),
                validUser.phoneNumber(),
                validUser.email(),
                new BigDecimal("-1"),
                validUser.role()
        );
        String expectedErrorMessage = "El salario debe estar entre 0 y 15000000";

        StepVerifier.create(userUseCase.createUser(userWithInvalidSalary))
                .expectErrorMessage("Errores en el formulario de registro: " + expectedErrorMessage)
                .verify();
    }

    @Test
    void createUser_SalaryAboveUpperBound_ThrowsIllegalArgumentException() {
        User userWithInvalidSalary = new User(
                validUser.id(),
                validUser.documentNumber(),
                validUser.firstName(),
                validUser.lastName(),
                validUser.birthDate(),
                validUser.address(),
                validUser.phoneNumber(),
                validUser.email(),
                new BigDecimal("15000001"),
                validUser.role()
        );
        String expectedErrorMessage = "El salario debe estar entre 0 y 15000000";

        StepVerifier.create(userUseCase.createUser(userWithInvalidSalary))
                .expectErrorMessage("Errores en el formulario de registro: " + expectedErrorMessage)
                .verify();
    }

    // --- Tests for other methods ---

    @Test
    void getUserById_UserFound_ReturnsUser() {
        when(userRepository.getUserById("123")).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.getUserById("123"))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void getUserById_UserNotFound_ReturnsEmpty() {
        when(userRepository.getUserById("unknown-id")).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserById("unknown-id"))
                .expectNextCount(0)
                .verifyComplete();
    }
    
    @Test
    void getUserByDocumentNumber_UserFound_ReturnsUser() {
        when(userRepository.getUserByDocumentNumber("123456789")).thenReturn(Mono.just(validUser));
        
        StepVerifier.create(userUseCase.getUserByDocumentNumber("123456789"))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void getUserByDocumentNumber_UserNotFound_ReturnsEmpty() {
        when(userRepository.getUserByDocumentNumber("987654321")).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.getUserByDocumentNumber("987654321"))
                .expectNextCount(0)
                .verifyComplete();
    }
}