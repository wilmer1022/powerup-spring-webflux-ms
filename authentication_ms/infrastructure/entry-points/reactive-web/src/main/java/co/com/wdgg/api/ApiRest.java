package co.com.wdgg.api;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.wdgg.api.dto.MessageResponse;
import co.com.wdgg.api.exceptions.UserNotFoundException;
import co.com.wdgg.api.validators.UserValidator;
import co.com.wdgg.model.user.User;
import co.com.wdgg.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller for managing user-related operations.
 * This class handles all API endpoints for creating, retrieving, and
 * validating users within the system. It leverages Spring WebFlux for
 * reactive and non-blocking interactions.
 *
 * @author wilmer1022
 * @version 0.0.1
 * @since 2023-08-25
 */
@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {

    private final UserUseCase userUseCase;
    private final UserValidator userValidator;

    /**
     * Retrieves a user by their unique ID.
     * This method handles a GET request to find a user using their ID.
     * It returns a reactive Mono containing the response entity.
     *
     * @param id The unique identifier of the user to be retrieved.
     * @return the {@link User} object if found
     */
    @GetMapping()
    public Mono<ResponseEntity<MessageResponse<User>>> getUserById(@RequestParam("id") String id) {
        return userUseCase.getUserById(id)
                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                        .body(MessageResponse.<User>builder()
                                .message("Usuario encontrado")
                                .data(userRetrieved)
                                .build()))
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario con id " + id + " no encontrado")));
    }

    /**
     * Retrieves a user by their document number.
     * This method handles a GET request to find a user using their document number.
     * It returns a reactive Mono containing the response entity.
     *
     * @param documentNumber The unique document number of the user.
     * @return the {@link User} object if found
     */
    @GetMapping("/buscar")
    public Mono<ResponseEntity<MessageResponse<User>>> getUserByDocumentNumber(@RequestParam("document_number") String documentNumber) {
        return userUseCase.getUserByDocumentNumber(documentNumber)
                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                        .body(MessageResponse.<User>builder()
                                .message("Usuario encontrado")
                                .data(userRetrieved)
                                .build()))
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario con numero de documento " + documentNumber + " no encontrado")));     
    }
    
    /**
     * Creates a new user.
     * This method handles a POST request to create a new user.
     * It returns a reactive Mono containing the response entity.
     *
     * @param user The user object to be created.
     * @return the {@link User} object if created successfully
     */
    @PostMapping()
    public Mono<ResponseEntity<MessageResponse<User>>> createUser(@RequestBody User user) {
        return Mono.just(user)
                .doOnNext(u -> userValidator.validate(user, new BeanPropertyBindingResult(user, "user")))
                .flatMap(validatedUser -> userUseCase.createUser(validatedUser)
                        .map(createdUser -> ResponseEntity.status(HttpStatus.CREATED)
                                .body(MessageResponse.<User>builder()
                                        .message("Usuario creado")
                                        .data(createdUser)
                                        .build())))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.error(new IllegalArgumentException(ex.getMessage())));
    }
}
