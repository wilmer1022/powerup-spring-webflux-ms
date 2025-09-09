package co.com.wdgg.usecase.user;

import co.com.wdgg.model.user.gateways.UserRepository;
import co.com.wdgg.usecase.user.validators.UserValidator;
import co.com.wdgg.usecase.userrole.UserRoleUseCase;

import java.util.ArrayList;
import java.util.List;

import co.com.wdgg.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UserUseCase {

    private final UserRepository userRepository;
    private final UserRoleUseCase userRoleUseCase;
    public UserUseCase(UserRepository userRepository, UserRoleUseCase userRoleUseCase) {
        this.userRepository = userRepository;
        this.userRoleUseCase = userRoleUseCase;
    }

    public Mono<User> getUserById(String id) {
        return userRepository.getUserById(id);
    }

    public Mono<User> getUserByDocumentNumber(String documentNumber) {
        return userRepository.getUserByDocumentNumber(documentNumber);
    }

    public Mono<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public Mono<User> createUser(User user) {
        return Mono.just(user)
                .flatMap(u -> {
                    final UserValidator userValidator = new UserValidator(u);
                    List<String> errors = new ArrayList<>();

                    Mono.justOrEmpty(userValidator.validateDocumentNumber()).subscribe(errors::add);
                    Mono.justOrEmpty(userValidator.validateFirstName()).subscribe(errors::add);
                    Mono.justOrEmpty(userValidator.validateLastName()).subscribe(errors::add);
                    Mono.justOrEmpty(userValidator.validateEmail()).subscribe(errors::add);
                    Mono.justOrEmpty(userValidator.validatePassword()).subscribe(errors::add);
                    Mono.justOrEmpty(userValidator.validateSalary()).subscribe(errors::add);
                    Mono.justOrEmpty(userValidator.validateEmailForm()).subscribe(errors::add);
                    Mono.justOrEmpty(userValidator.validateSalaryForm()).subscribe(errors::add);

                    if (!errors.isEmpty()) {
                        String errorMessage = String.join(", ", errors);
                        return Mono.error(
                                new IllegalArgumentException("Errores en el formulario de registro: " + errorMessage));
                    }

                    return userRepository.getUserByEmail(u.email())
                            .flatMap(userRetrieved -> Mono.error(new IllegalArgumentException(
                                    "El correo electrónico " + u.email() + " ya está registrado")))
                            .switchIfEmpty(
                                    Mono.defer(() -> userRoleUseCase.getUserRoleByRole("CLIENTE")
                                            .switchIfEmpty(Mono.error(
                                                    new IllegalStateException("El rol por defecto 'CLIENTE' no existe")))
                                            .flatMap(userRole -> {
                                                final User userWithRole = new User(
                                                        u.id(),
                                                        u.documentNumber(),
                                                        u.firstName(),
                                                        u.lastName(),
                                                        u.birthDate(),
                                                        u.address(),
                                                        u.phoneNumber(),
                                                        u.email(),
                                                        u.password(),
                                                        u.salary(),
                                                        userRole);
                                                return userRepository.createUser(userWithRole);
                                            })))
                            .cast(User.class);
                });
    }

    public Mono<User> signInUser(String email, String rawPassword) {
        return userRepository.getUserByEmail(email)
                .flatMap(user -> {
                    if (userRepository.validateUserPassword(user.password(), rawPassword).block()) return Mono.just(user);

                    return Mono.error(new IllegalArgumentException("El correo electrónico y/o contraseña son incorrectos"));
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El correo electrónico y/o contraseña son incorrectos")));
    }

    public Flux<User> getUsersByEmails(List<String> emails) {
        return userRepository.getUsersByEmails(emails);
    }
}
