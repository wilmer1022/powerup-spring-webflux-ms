package co.com.wdgg.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;

import co.com.wdgg.model.user.User;
import co.com.wdgg.r2dbc.entities.UserEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveRepositoryAdapter userReactiveRepositoryAdapter;

    @Mock
    UserReactiveRepository userReactiveRepository;

    @Mock
    ObjectMapper objectMapper;

    @Test
    void mustFindUserById_WhenUserExists() {
        UUID userId = UUID.fromString("ec2736af-0f89-4e78-841d-4383a8672262");
        UserEntity mockUser = new UserEntity();
        when(userReactiveRepository.findById(userId)).thenReturn(Mono.just(mockUser));

        Mono<User> result = userReactiveRepositoryAdapter.getUserById(userId.toString());

        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    verify(userReactiveRepository, times(1)).findById(userId);
                    return user.equals(new User(mockUser.getId(),
                            mockUser.getDocumentNumber(),
                            mockUser.getFirstName(),
                            mockUser.getLastName(),
                            mockUser.getBirthDate(),
                            mockUser.getAddress(),
                            mockUser.getPhoneNumber(),
                            mockUser.getEmail(),
                            mockUser.getPassword(),
                            mockUser.getSalary(),
                            null));
                })
                .verifyComplete();
    }

    @Test
    void mustFindUserById_WhenUserDoesNotExist() {
        UUID userId = UUID.fromString("ec2736af-0f89-4e78-841d-4383a8672262");
        when(userReactiveRepository.findById(userId)).thenReturn(Mono.empty());

        Mono<User> result = userReactiveRepositoryAdapter.getUserById(userId.toString());

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void mustFindUserByIdAndMapCorrectly() {
        UUID userId = UUID.fromString("ec2736af-0f89-4e78-841d-4383a8672262");
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("John");

        when(userReactiveRepository.findById(userId)).thenReturn(Mono.just(userEntity));
        Mono<User> result = userReactiveRepositoryAdapter.getUserById(userId.toString());

        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    return userEntity.getFirstName().equals(user.firstName());
                })
                .verifyComplete();
    }

    @Test
    void mustFindUserByDocumentNumber_WhenUserExists() {
        UserEntity mockUser = new UserEntity();
        when(userReactiveRepository.findByDocumentNumber("123456789")).thenReturn(Mono.just(mockUser));

        Mono<User> result = userReactiveRepositoryAdapter.getUserByDocumentNumber("123456789");

        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    verify(userReactiveRepository, times(1)).findByDocumentNumber("123456789");
                    return user.equals(new User(mockUser.getId(),
                            mockUser.getDocumentNumber(),
                            mockUser.getFirstName(),
                            mockUser.getLastName(),
                            mockUser.getBirthDate(),
                            mockUser.getAddress(),
                            mockUser.getPhoneNumber(),
                            mockUser.getEmail(),
                            mockUser.getPassword(),
                            mockUser.getSalary(),
                            null));
                })
                .verifyComplete();
    }

    @Test
    void mustFindUserByDocumentNumber_WhenUserDoesNotExist() {
        when(userReactiveRepository.findByDocumentNumber("123456789")).thenReturn(Mono.empty());

        Mono<User> result = userReactiveRepositoryAdapter.getUserByDocumentNumber("123456789");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void mustFindUserByDocumentNumberAndMapCorrectly() {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("John");

        when(userReactiveRepository.findByDocumentNumber("123456789")).thenReturn(Mono.just(userEntity));
        Mono<User> result = userReactiveRepositoryAdapter.getUserByDocumentNumber("123456789");

        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    return userEntity.getFirstName().equals(user.firstName());
                })
                .verifyComplete();
    }

    @Test
    void mustFindUserByEmail_WhenUserExists() {
        when(userReactiveRepository.findByEmail("email@email.com")).thenReturn(Mono.just(new UserEntity()));

        Mono<UserEntity> result = userReactiveRepository.findByEmail("email@email.com");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(new UserEntity()))
                .verifyComplete();
    }

    @Test
    void mustFindUserByEmail_WhenUserDoesNotExist() {
        when(userReactiveRepository.findByEmail("email@email.com")).thenReturn(Mono.empty());

        Mono<UserEntity> result = userReactiveRepository.findByEmail("email@email.com");

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void mustFindUserByEmailAndMapCorrectly() {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("John");

        when(userReactiveRepository.findByEmail("email@email.com")).thenReturn(Mono.just(userEntity));
        Mono<User> result = userReactiveRepositoryAdapter.getUserByEmail("email@email.com");

        StepVerifier.create(result)
                .expectNextMatches(user -> {
                    return userEntity.getFirstName().equals(user.firstName());
                })
                .verifyComplete();
    }

    @Test
    void mustCreateUser() {
        when(userReactiveRepository.save(any(UserEntity.class))).thenReturn(Mono.just(new UserEntity()));

        Mono<UserEntity> result = userReactiveRepository.save(new UserEntity());

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(new UserEntity()))
                .verifyComplete();
    }
}
