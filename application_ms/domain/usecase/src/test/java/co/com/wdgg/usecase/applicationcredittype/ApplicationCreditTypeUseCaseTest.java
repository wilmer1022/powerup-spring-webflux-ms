package co.com.wdgg.usecase.applicationcredittype;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.model.applicationcredittype.gateways.ApplicationCreditTypeRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationCreditTypeUseCaseTest {
    
    @Mock
    private ApplicationCreditTypeRepository applicationCreditTypeRepository;

    @InjectMocks
    private ApplicationCreditTypeUseCase applicationCreditTypeUseCase;

    private ApplicationCreditType validApplicationCreditType;

    @BeforeEach
    void setUp() {
        validApplicationCreditType = new ApplicationCreditType(null, "CREDITO", new BigDecimal("0"), new BigDecimal("10000000000"), new BigDecimal("0.05"), true);
    }

    // --- Tests for getApplicationCreditTypeById ---

    @Test
    void getApplicationCreditTypeById_Found_ReturnsApplicationCreditType() {
        when(applicationCreditTypeRepository.getApplicationCreditTypeById("CREDITO"))
                .thenReturn(Mono.just(validApplicationCreditType));

        Mono<ApplicationCreditType> result = applicationCreditTypeUseCase.getApplicationCreditTypeById("CREDITO");

        StepVerifier.create(result)
                .expectNext(validApplicationCreditType)
                .verifyComplete();
    }

    @Test
    void getApplicationCreditTypeById_NotFound_ReturnsNull() {
        when(applicationCreditTypeRepository.getApplicationCreditTypeById("CREDITO"))
                .thenReturn(Mono.empty());

        Mono<ApplicationCreditType> result = applicationCreditTypeUseCase.getApplicationCreditTypeById("CREDITO");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}
