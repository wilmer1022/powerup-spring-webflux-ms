package co.com.wdgg.r2dbc;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.com.wdgg.model.user.gateways.UserRepository;
import co.com.wdgg.model.user.User;

import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter implements UserRepository {

    private final MyReactiveRepository repository;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<User> getUserById(String id) {
        return repository.findById(UUID.fromString(id))
                .map(this::toDomain);
    }

    @Override
    public Mono<User> getUserByDocumentNumber(String documentNumber) {
        return repository.findByDocumentNumber(documentNumber)
                .map(this::toDomain);
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Mono<User> createUser(User user) {
        return repository.save(toEntity(user))
                .map(this::toDomain);
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
                user.salary());

    }

    private User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getDocumentNumber(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDate(),
                userEntity.getAddress(),
                userEntity.getPhoneNumber(),
                userEntity.getEmail(),
                userEntity.getSalary());
    }
}
