package co.com.wdgg.usecase.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.application.gateways.ApplicationRepository;
import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import co.com.wdgg.usecase.applicationcredittype.ApplicationCreditTypeUseCase;
import co.com.wdgg.usecase.applicationstatus.ApplicationStatusUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationUseCaseTest {
    
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationCreditTypeUseCase applicationCreditTypeUseCase;

    @Mock
    private ApplicationStatusUseCase applicationStatusUseCase;

    @InjectMocks
    private ApplicationUseCase applicationUseCase;

    private Application validApplication;
    private ApplicationCreditType validApplicationCreditType;
    private ApplicationStatus validApplicationStatus;

    @BeforeEach
    void setUp() {
        validApplicationCreditType = new ApplicationCreditType(
            UUID.randomUUID(),
            "CREDITO",
            new BigDecimal("0"),
            new BigDecimal("10000000000"),
            new BigDecimal("0.05"),
            true
        );
        validApplicationStatus = new ApplicationStatus(
            UUID.randomUUID(),
            "PENDIENTE",
            "Pendiente"
        );
        validApplication = new Application(
            UUID.randomUUID(),
            "user@email.com",
            new BigDecimal("5000000"),
            LocalDate.of(2023, 1, 1),
            validApplicationStatus,
            validApplicationCreditType
        );
    }

    // --- Tests for createApplication ---

    @Test
    void createApplication_ValidApplication_ReturnsCreatedApplication() {
        when(applicationRepository.createApplication(validApplication))
            .thenReturn(Mono.just(validApplication));

        when(applicationCreditTypeUseCase.getApplicationCreditTypeById(validApplication.applicationCreditType().id().toString()))
            .thenReturn(Mono.just(validApplicationCreditType));

        when(applicationStatusUseCase.getApplicationStatusByStatus("PENDIENTE"))
            .thenReturn(Mono.just(validApplicationStatus));

        StepVerifier.create(applicationUseCase.createApplication(validApplication))
                .expectNextMatches(application ->
                    application.userEmail().equals(validApplication.userEmail()) &&
                    application.amount().equals(validApplication.amount()) &&
                    application.creditPeriod().equals(validApplication.creditPeriod()) &&
                    application.applicationCreditType().equals(validApplication.applicationCreditType()) &&
                    application.applicationStatus().equals(validApplication.applicationStatus())
                )
                .verifyComplete();
    }

    // --- Tests for validation of data ---

    @Test
    void createApplication_InvaliduserEmail_ThrowsIllegalArgumentException() {
        Application applicationWithInvaliduserEmail = new Application(
            validApplication.id(),
            null,
            validApplication.amount(),
            validApplication.creditPeriod(),
            validApplication.applicationStatus(),
            validApplication.applicationCreditType()
        );
        String expectedErrorMessage = "Errores en el formulario de registro: El correo electrónico es obligatorio";

        StepVerifier.create(applicationUseCase.createApplication(applicationWithInvaliduserEmail))
            .expectErrorMessage(expectedErrorMessage)
            .verify();
    }

    @Test
    void createApplication_InvalidCreditType_ThrowsIllegalArgumentException() {
        Application applicationWithInvalidCreditType = new Application(
            validApplication.id(),
            validApplication.userEmail(),
            validApplication.amount(),
            validApplication.creditPeriod(),
            validApplication.applicationStatus(),
            null
        );
        String expectedErrorMessage = "Errores en el formulario de registro: El tipo de credito es obligatorio";

        StepVerifier.create(applicationUseCase.createApplication(applicationWithInvalidCreditType))
            .expectErrorMessage(expectedErrorMessage)
            .verify();
    }

    @Test
    void createApplication_InvalidAmountForm_ThrowsIllegalArgumentException() {
        Application applicationWithInvalidAmountForm = new Application(
            validApplication.id(),
            validApplication.userEmail(),
            new BigDecimal("10000000001"),
            validApplication.creditPeriod(),
            validApplication.applicationStatus(),
            validApplication.applicationCreditType()
        );
        String expectedErrorMessage = "Errores en el formulario de registro: El monto debe estar entre 0 y 10000000000";

        StepVerifier.create(applicationUseCase.createApplication(applicationWithInvalidAmountForm))
            .expectErrorMessage(expectedErrorMessage)
            .verify();
    }

    // --- Tests for other methods ---

    @Test
    void getApplicationById_UserFound_ReturnsUser() {
        when(applicationRepository.getApplicationById("123")).thenReturn(Mono.just(validApplication));

        StepVerifier.create(applicationUseCase.getApplicationById("123"))
            .expectNext(validApplication)
            .verifyComplete();
    }

    @Test
    void getApplicationById_UserNotFound_ReturnsEmpty() {
        when(applicationRepository.getApplicationById("unknown-id")).thenReturn(Mono.empty());

        StepVerifier.create(applicationUseCase.getApplicationById("unknown-id"))
            .expectNextCount(0)
            .verifyComplete();
    }

    @Test
    void getApplicationByuserEmail_UserFound_ReturnsUser() {
        when(applicationRepository.getApplicationByUserEmail("user@email.com")).thenReturn(Flux.just(validApplication));

        StepVerifier.create(applicationUseCase.getApplicationByUserEmail("user@email.com"))
            .expectNext(validApplication)
            .verifyComplete();
    }

    @Test
    void getApplicationByuserEmail_UserNotFound_ReturnsEmpty() {
        when(applicationRepository.getApplicationByUserEmail("user@email.com")).thenReturn(Flux.empty());

        StepVerifier.create(applicationUseCase.getApplicationByUserEmail("user@email.com"))
            .expectNextCount(0)
            .verifyComplete();
    }
}
