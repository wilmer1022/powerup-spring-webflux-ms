package co.com.wdgg.usecase.applicationstatus;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.wdgg.model.applicationstatus.ApplicationStatus;
import co.com.wdgg.model.applicationstatus.gateways.ApplicationStatusRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationStatusUseCaseTest {
 
    @Mock
    private ApplicationStatusRepository applicationStatusRepository;

    @InjectMocks
    private ApplicationStatusUseCase applicationStatusUseCase;

    private ApplicationStatus validApplicationStatus;

    @BeforeEach
    void setUp() {
        validApplicationStatus = new ApplicationStatus(UUID.randomUUID(), "PENDIENTE", "Pendiente");
    }

    // --- Tests for getApplicationStatusByStatus ---

    @Test
    void getApplicationStatusByStatus_Found_ReturnsApplicationStatus() {
        when(applicationStatusRepository.getApplicationStatusByStatus("PENDIENTE"))
                .thenReturn(Mono.just(validApplicationStatus));

        Mono<ApplicationStatus> result = applicationStatusUseCase.getApplicationStatusByStatus("PENDIENTE");

        StepVerifier.create(result)
                .expectNext(validApplicationStatus)
                .verifyComplete();
    }

    @Test
    void getApplicationStatusByStatus_NotFound_ReturnsEmptyMono() {
        when(applicationStatusRepository.getApplicationStatusByStatus("PENDIENTE"))
                .thenReturn(Mono.empty());

        Mono<ApplicationStatus> result = applicationStatusUseCase.getApplicationStatusByStatus("PENDIENTE");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    // --- Tests for getApplicationStatusById ---

    @Test
    void getApplicationStatusById_Found_ReturnsApplicationStatus() {
        when(applicationStatusRepository.getApplicationStatusById("PENDIENTE"))
                .thenReturn(Mono.just(validApplicationStatus));

        Mono<ApplicationStatus> result = applicationStatusUseCase.getApplicationStatusById("PENDIENTE");

        StepVerifier.create(result)
                .expectNext(validApplicationStatus)
                .verifyComplete();
    }

    @Test
    void getApplicationStatusById_NotFound_ReturnsEmptyMono() {
        when(applicationStatusRepository.getApplicationStatusById("PENDIENTE"))
                .thenReturn(Mono.empty());

        Mono<ApplicationStatus> result = applicationStatusUseCase.getApplicationStatusById("PENDIENTE");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
