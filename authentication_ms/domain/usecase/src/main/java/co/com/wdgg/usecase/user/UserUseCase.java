package co.com.wdgg.usecase.user;

import co.com.wdgg.model.user.gateways.UserRepository;
import co.com.wdgg.model.user.User;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> getUserById(String id) {
        return userRepository.getUserById(id);
    }

    public Mono<User> getUserByDocumentNumber(String documentNumber) {
        return userRepository.getUserByDocumentNumber(documentNumber);
    }

    public Mono<User> createUser(User user) {
        return userRepository.getUserByEmail(user.email())
                .flatMap(userRetrieved -> Mono.error(new IllegalArgumentException("El correo electrónico " + user.email() + " ya está registrado")))
                .switchIfEmpty(Mono.defer(() -> userRepository.createUser(user)))
                .cast(User.class);
    }
}
