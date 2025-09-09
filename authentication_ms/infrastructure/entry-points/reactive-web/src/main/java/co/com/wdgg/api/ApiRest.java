package co.com.wdgg.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.wdgg.api.dto.MessageResponse;
import co.com.wdgg.api.dto.SignInRequest;
import co.com.wdgg.api.dto.SignInResponse;
import co.com.wdgg.api.dto.UserDTO;
import co.com.wdgg.api.dto.UserMapper;
import co.com.wdgg.api.dto.UserRequest;
import co.com.wdgg.api.dto.UserResponse;
import co.com.wdgg.api.exceptions.UserNotFoundException;
import co.com.wdgg.api.services.JwtService;
import co.com.wdgg.model.user.User;
import co.com.wdgg.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Flux;
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
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiRest {

        private final UserUseCase userUseCase;
        private final JwtService jwtService;
        private final UserMapper userMapper;

        public ApiRest(UserUseCase userUseCase, JwtService jwtService, UserMapper userMapper) {
                this.userUseCase = userUseCase;
                this.jwtService = jwtService;
                this.userMapper = userMapper;
        }

        /**
         * Retrieves a user by their unique ID.
         * This method handles a GET request to find a user using their ID.
         * It returns a reactive Mono containing the response entity.
         *
         * @param id The unique identifier of the user to be retrieved.
         * @return the {@link User} object if found
         */
        @Operation(summary = "Buscar un usuario por su ID", description = "Recibe el ID del usuario (String) para buscarlo.", tags = {
                        "Usuarios" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @GetMapping("/usuarios")
        public Mono<ResponseEntity<MessageResponse<UserResponse>>> getUserById(@RequestParam("id") String id) {
                return userUseCase.getUserById(id)
                                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<UserResponse>builder()
                                                                .message("Usuario encontrado")
                                                                .data(userMapper.toResponse(userRetrieved))
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
        @Operation(summary = "Buscar un usuario por su documento", description = "Recibe un número de documento (String) para buscarlo.", tags = {
                        "Usuarios" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @GetMapping("/usuarios/buscar")
        public Mono<ResponseEntity<MessageResponse<UserResponse>>> getUserByDocumentNumber(
                        @RequestParam("document_number") String documentNumber) {
                return userUseCase.getUserByDocumentNumber(documentNumber)
                                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<UserResponse>builder()
                                                                .message("Usuario encontrado")
                                                                .data(userMapper.toResponse(userRetrieved))
                                                                .build()))
                                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario con numero de documento "
                                                + documentNumber + " no encontrado")));
        }

        /**
         * Retrieves a user by their email.
         * This method handles a GET request to find a user using their email.
         * It returns a reactive Mono containing the response entity.
         *
         * @param email
         * @return
         */
        @Operation(summary = "Buscar un usuario por su correo electrónico", description = "Recibe un correo electrónico (String) para buscarlo.", tags = {
                        "Usuarios" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @GetMapping("/usuarios/buscar-por-email")
        public Mono<ResponseEntity<MessageResponse<UserResponse>>> getUserByEmail(@RequestParam("email") String email) {
                return userUseCase.getUserByEmail(email)
                                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<UserResponse>builder()
                                                                .message("Usuario encontrado")
                                                                .data(userMapper.toResponse(userRetrieved))
                                                                .build()))
                                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario con correo electrónico "
                                                + email + " no encontrado")));
        }

        /**
         * Creates a new user.
         * This method handles a POST request to create a new user.
         * It returns a reactive Mono containing the response entity.
         *
         * @param user The user object to be created.
         * @return the {@link User} object if created successfully
         */
        @Operation(summary = "Registrar un nuevo usuario", description = "Recibe un objeto UserRequest para guardar la informacion del usuario en la base de datos.", tags = {
                        "Usuarios" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuario guardado correctamente"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @PostMapping("/usuarios")
        public Mono<ResponseEntity<MessageResponse<UserResponse>>> createUser(@RequestBody UserRequest userRequest) {
                return userUseCase.createUser(new User(null, userRequest.documentNumber(), userRequest.firstName(),
                                userRequest.lastName(), userRequest.birthDate(), userRequest.address(),
                                userRequest.phoneNumber(), userRequest.email(), userRequest.password(),
                                userRequest.salary(), null))
                                .map(createdUser -> ResponseEntity.status(HttpStatus.CREATED)
                                                .body(MessageResponse.<UserResponse>builder()
                                                                .message("Usuario creado")
                                                                .data(userMapper.toResponse(createdUser))
                                                                .build()));
        }

        /**
         * Sign in a user by their email and password.
         * This method handles a POST request to sign in a user.
         * It returns a reactive Mono containing the response entity.
         *
         * @param email    The email of the user to be signed in.
         * @param password The password of the user to be signed in.
         * @return the {@link User} object if signed in successfully
         */
        @Operation(summary = "Iniciar sesión de un usuario", description = "Recibe el correo electrónico y la contraseña del usuario para iniciar sesión.", tags = {
                        "Autenticación" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @PostMapping("/login")
        public Mono<ResponseEntity<MessageResponse<SignInResponse>>> signInUser(
                        @RequestBody SignInRequest singInRequest) {
                return userUseCase.signInUser(singInRequest.email(), singInRequest.password())
                                .map(userRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<SignInResponse>builder()
                                                                .message("Inicio de sesión exitoso")
                                                                .data(new SignInResponse(
                                                                                jwtService.generateToken(new UserDTO(
                                                                                                userRetrieved))))
                                                                .build()));
        }

        /**
         * Retrieves a user by their email.
         * This method handles a GET request to find users using their emails.
         * It returns a reactive Flux containing the response entity.
         *
         * @param emails
         * @return List of {@link User} object if found
         */
        @Operation(summary = "Buscar un usuario por su correo electrónico", description = "Recibe una lista de correos electrónicos (List<String>) para buscarlos.", tags = {
                        "Usuarios" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuarios encontrado"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @GetMapping("/usuarios/emails")
        public Flux<UserResponse> getUserByEmails(@RequestParam("emails") List<String> emails) {
                return userUseCase.getUsersByEmails(emails)
                                .map(userMapper::toResponse);
        }
}
