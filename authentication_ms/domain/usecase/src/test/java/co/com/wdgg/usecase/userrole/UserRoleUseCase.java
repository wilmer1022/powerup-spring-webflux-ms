package co.com.wdgg.usecase.userrole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.wdgg.model.userrole.UserRole;
import co.com.wdgg.model.userrole.gateways.UserRoleRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserRoleUseCaseTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleUseCase userRoleUseCase;

    private UserRole userRole;

    @BeforeEach
    void setUp() {
        userRole = new UserRole(UUID.randomUUID(), "ADMIN", "ADMIN");
    }

    // --- Tests for getUserRoleById ---

    @Test
    void getUserRoleById_Found_ReturnsUserRole() {
        when(userRoleRepository.getUserRoleById("ADMIN"))
                .thenReturn(Mono.just(userRole));

        StepVerifier.create(userRoleUseCase.getUserRoleById("ADMIN"))
                .expectNext(userRole)
                .verifyComplete();
    }

    @Test
    void getUserRoleById_NotFound_ReturnsEmptyMono() {
        when(userRoleRepository.getUserRoleById("non-existent-id"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRoleUseCase.getUserRoleById("non-existent-id"))
                .expectNextCount(0)
                .verifyComplete();
    }

    // --- Tests for getUserRoleByRole ---

    @Test
    void getUserRoleByRole_Found_ReturnsUserRole() {
        when(userRoleRepository.getUserRoleByRole("ADMIN"))
                .thenReturn(Mono.just(userRole));

        StepVerifier.create(userRoleUseCase.getUserRoleByRole("ADMIN"))
                .expectNext(userRole)
                .verifyComplete();
    }

    @Test
    void getUserRoleByRole_NotFound_ReturnsEmptyMono() {
        when(userRoleRepository.getUserRoleByRole("GUEST"))
                .thenReturn(Mono.empty());

        StepVerifier.create(userRoleUseCase.getUserRoleByRole("GUEST"))
                .expectNextCount(0)
                .verifyComplete();
    }
}