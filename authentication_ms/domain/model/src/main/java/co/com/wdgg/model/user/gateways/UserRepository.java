package co.com.wdgg.model.user.gateways;

import java.util.List;

import co.com.wdgg.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> getUserById(String id);

    Mono<User> getUserByDocumentNumber(String documentNumber);

    Mono<User> getUserByEmail(String email);

    Mono<User> createUser(User user);

    Mono<Boolean> validateUserPassword(String chiperPassword, String password);

    Flux<User> getUsersByEmails(List<String> emails);
}
