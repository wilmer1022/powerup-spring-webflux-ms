package co.com.wdgg.api;

import lombok.AllArgsConstructor;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.wdgg.api.dto.ApplicationMapper;
import co.com.wdgg.api.dto.ApplicationRequest;
import co.com.wdgg.api.dto.ApplicationResponse;
import co.com.wdgg.api.dto.MessageResponse;
import co.com.wdgg.api.exceptions.ApplicationNotFoundException;
import co.com.wdgg.api.services.AuthService;
import co.com.wdgg.model.application.Application;
import co.com.wdgg.usecase.application.ApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * REST controller for managing application-related operations.
 * This class handles all API endpoints for creating, retrieving, and
 * validating applications within the system. It leverages Spring WebFlux for
 * reactive and non-blocking interactions.
 *
 * @author wilmer1022
 * @version 0.0.1
 * @since 2023-08-25
 */
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiRest {

        private final ApplicationUseCase applicationUseCase;
        private final ApplicationMapper applicationMapper;
        private final AuthService authService;

        public ApiRest(ApplicationUseCase applicationUseCase, ApplicationMapper applicationMapper, AuthService authService) {
                this.applicationUseCase = applicationUseCase;
                this.applicationMapper = applicationMapper;
                this.authService = authService;
        }

        /**
         * Retrieves an application by its unique ID.
         * This method handles a GET request to find an application using its ID.
         * It returns a reactive Mono containing the response entity.
         * 
         * @param id
         * @return
         */
        @Operation(summary = "Buscar una solicitud por su ID", description = "Recibe el ID del usuario (String) para buscarla.", tags = {
                        "Solicitudes" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @GetMapping()
        public Mono<ResponseEntity<MessageResponse<ApplicationResponse>>> getApplicationById(
                        @RequestParam("id") String id) {
                return applicationUseCase.getApplicationById(id)
                                .map(applicationRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<ApplicationResponse>builder()
                                                                .message("Solicitud encontrada")
                                                                .data(applicationMapper
                                                                                .toResponse(applicationRetrieved))
                                                                .build()))
                                .switchIfEmpty(Mono.error(new ApplicationNotFoundException(
                                                "Solicitud con id " + id + " no encontrada")));
        }

        /**
         * Retrieves an application by its user email.
         * This method handles a GET request to find an application using its user
         * email.
         * It returns a reactive Mono containing the response entity.
         * 
         * @param userEmail
         * @return
         */
        @Operation(summary = "Buscar una solicitud por su correo electrónico", description = "Recibe un correo electrónico (String) para buscarla.", tags = {
                        "Solicitudes" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @GetMapping("/buscar")
        public Mono<ResponseEntity<MessageResponse<List<Application>>>> getApplicationByUserEmail(
                        @RequestParam("user_email") String userEmail) {
                return applicationUseCase.getApplicationByUserEmail(userEmail)
                                .collectList()
                                .flatMap(applicationRetrieved -> {
                                        if (applicationRetrieved.isEmpty()) {
                                                return Mono.error(new ApplicationNotFoundException(
                                                                "Solicitudes con el correo electrónico " + userEmail
                                                                                + " no encontradas"));
                                        }
                                        return Mono.just(ResponseEntity.status(HttpStatus.OK)
                                                        .body(MessageResponse.<List<Application>>builder()
                                                                        .message("Solicitudes encontradas")
                                                                        .data(applicationRetrieved)
                                                                        .build()));
                                });
        }

        /**
         * Creates a new application.
         * This method handles a POST request to create a new application.
         * It returns a reactive Mono containing the response entity.
         * 
         * @param application
         * @return
         */
        @Operation(summary = "Crear una nueva solicitud", description = "Recibe un objeto ApplicationRequest para crear una nueva solicitud.", tags = {
                        "Solicitudes" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @PostMapping()
        public Mono<ResponseEntity<MessageResponse<ApplicationResponse>>> createApplication(
                        @RequestBody ApplicationRequest applicationRequest,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

                Application application = applicationMapper.toApplication(applicationRequest);

                return authService.validateUserExists(application.userEmail(), authorizationHeader)
                                .flatMap(user -> applicationUseCase.createApplication(application))
                                .map(createdApplication -> ResponseEntity.status(HttpStatus.CREATED)
                                                .body(MessageResponse.<ApplicationResponse>builder()
                                                                .message("Solicitud creada")
                                                                .data(applicationMapper.toResponse(createdApplication))
                                                                .build()));
        }
}
