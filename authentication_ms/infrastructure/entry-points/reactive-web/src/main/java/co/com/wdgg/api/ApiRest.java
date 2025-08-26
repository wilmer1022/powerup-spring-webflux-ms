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
import co.com.wdgg.api.dto.UserRequest;
import co.com.wdgg.api.dto.UserResponse;
import co.com.wdgg.api.exceptions.UserNotFoundException;
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

        /**
         * Retrieves a user by their unique ID.
         * This method handles a GET request to find a user using their ID.
         * It returns a reactive Mono containing the response entity.
         *
         * @param id The unique identifier of the user to be retrieved.
         * @return the {@link User} object if found
         */
        @GetMapping()
        public Mono<ResponseEntity<MessageResponse<UserResponse>>> getUserById(@RequestParam("id") String id) {
                return userUseCase.getUserById(id)
                                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<UserResponse>builder()
                                                                .message("Usuario encontrado")
                                                                .data(new UserResponse(
                                                                                userRetrieved.id(),
                                                                                userRetrieved.documentNumber(),
                                                                                userRetrieved.firstName(),
                                                                                userRetrieved.lastName(),
                                                                                userRetrieved.email(),
                                                                                userRetrieved.phoneNumber(),
                                                                                userRetrieved.address(),
                                                                                userRetrieved.birthDate(),
                                                                                userRetrieved.salary()))
                                                                .build()))
                                .switchIfEmpty(Mono.error(
                                                new UserNotFoundException("Usuario con id " + id + " no encontrado")));
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
        public Mono<ResponseEntity<MessageResponse<UserResponse>>> getUserByDocumentNumber(
                        @RequestParam("document_number") String documentNumber) {
                return userUseCase.getUserByDocumentNumber(documentNumber)
                                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<UserResponse>builder()
                                                                .message("Usuario encontrado")
                                                                .data(new UserResponse(
                                                                                userRetrieved.id(),
                                                                                userRetrieved.documentNumber(),
                                                                                userRetrieved.firstName(),
                                                                                userRetrieved.lastName(),
                                                                                userRetrieved.email(),
                                                                                userRetrieved.phoneNumber(),
                                                                                userRetrieved.address(),
                                                                                userRetrieved.birthDate(),
                                                                                userRetrieved.salary()))
                                                                .build()))
                                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario con numero de documento "
                                                + documentNumber + " no encontrado")));
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
        public Mono<ResponseEntity<MessageResponse<UserResponse>>> createUser(@RequestBody UserRequest userRequest) {
                return userUseCase.createUser(new User(null, userRequest.documentNumber(), userRequest.firstName(),
                                userRequest.lastName(), userRequest.birthDate(), userRequest.address(),
                                userRequest.phoneNumber(), userRequest.email(), userRequest.salary(), null))
                                .map(createdUser -> ResponseEntity.status(HttpStatus.CREATED)
                                                .body(MessageResponse.<UserResponse>builder()
                                                                .message("Usuario creado")
                                                                .data(new UserResponse(
                                                                                createdUser.id(),
                                                                                createdUser.documentNumber(),
                                                                                createdUser.firstName(),
                                                                                createdUser.lastName(),
                                                                                createdUser.email(),
                                                                                createdUser.phoneNumber(),
                                                                                createdUser.address(),
                                                                                createdUser.birthDate(),
                                                                                createdUser.salary()))
                                                                .build()));
        }
}
