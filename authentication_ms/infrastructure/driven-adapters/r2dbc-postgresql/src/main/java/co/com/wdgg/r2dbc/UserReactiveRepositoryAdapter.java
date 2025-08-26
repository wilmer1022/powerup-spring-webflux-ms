package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.wdgg.model.user.gateways.UserRepository;
import co.com.wdgg.model.userrole.UserRole;
import co.com.wdgg.model.userrole.gateways.UserRoleRepository;
import co.com.wdgg.r2dbc.entities.UserEntity;
import co.com.wdgg.model.user.User;

import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter implements UserRepository {

    private final UserReactiveRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    // Constructor actualizado para inyectar UserRoleUseCase
    public UserReactiveRepositoryAdapter(UserReactiveRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Mono<User> getUserById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .flatMap(this::loadUserRoleAndToDomain);
    }

    @Override
    public Mono<User> getUserByDocumentNumber(String documentNumber) {
        return userRepository.findByDocumentNumber(documentNumber)
                .flatMap(this::loadUserRoleAndToDomain);
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(this::loadUserRoleAndToDomain);
    }

    @Override
    public Mono<User> createUser(User user) {
        return userRepository.save(toEntity(user))
                .flatMap(this::loadUserRoleAndToDomain);
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.documentNumber(),
                user.firstName(),
                user.lastName(),
                user.birthDate(),
                user.address(),
                user.phoneNumber(),
                user.email(),
                user.salary(),
                user.role() != null ? user.role().id() : null,
                null
        );
    }

    private User toDomain(UserEntity userEntity, UserRole domainRole) {
        return new User(
                userEntity.getId(),
                userEntity.getDocumentNumber(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDate(),
                userEntity.getAddress(),
                userEntity.getPhoneNumber(),
                userEntity.getEmail(),
                userEntity.getSalary(),
                domainRole
        );
    }

    private Mono<User> loadUserRoleAndToDomain(UserEntity userEntity) {
        if (userEntity.getRoleId() == null) {
            return Mono.just(toDomain(userEntity, null));
        }

        return userRoleRepository.getUserRoleById(userEntity.getRoleId().toString())
                .map(domainRole -> toDomain(userEntity, domainRole))
                .defaultIfEmpty(toDomain(userEntity, null));
    }
}