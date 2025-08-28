package co.com.wdgg.api;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.wdgg.api.dto.ApplicationRequest;
import co.com.wdgg.api.dto.ApplicationResponse;
import co.com.wdgg.api.dto.MessageResponse;
import co.com.wdgg.api.exceptions.ApplicationNotFoundException;
import co.com.wdgg.api.service.AuthService;
import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.applicationcredittype.ApplicationCreditType;
import co.com.wdgg.usecase.application.ApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
@AllArgsConstructor
public class ApiRest {

        private final ApplicationUseCase applicationUseCase;
        private final AuthService authService;

        /**
         * Retrieves an application by its unique ID.
         * This method handles a GET request to find an application using its ID.
         * It returns a reactive Mono containing the response entity.
         * 
         * @param id
         * @return
         */
        @Operation(summary = "Buscar una solicitud por su ID", description = "Recibe el ID del usuario (String) para buscarla.", tags = {"Solicitudes"}, responses = {
                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
        })
        @GetMapping()
        public Mono<ResponseEntity<MessageResponse<ApplicationResponse>>> getApplicationById(@RequestParam("id") String id) {
                return applicationUseCase.getApplicationById(id)
                                .map(applicationRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<ApplicationResponse>builder()
                                                                .message("Solicitud encontrada")
                                                                .data(new ApplicationResponse(
                                                                        applicationRetrieved.id(),
                                                                        applicationRetrieved.userDocumentNumber(),
                                                                        applicationRetrieved.amount(),
                                                                        applicationRetrieved.creditPeriod(),
                                                                        applicationRetrieved.applicationCreditType() != null ? applicationRetrieved.applicationCreditType().creditType() : null,
                                                                        applicationRetrieved.applicationCreditType() != null ? applicationRetrieved.applicationCreditType().interestRate() : null,
                                                                        applicationRetrieved.applicationStatus() != null ? applicationRetrieved.applicationStatus().description() : null
                                                                ))
                                                                .build()))
                                .switchIfEmpty(Mono.error(new ApplicationNotFoundException(
                                                "Solicitud con id " + id + " no encontrada")));
        }

        /**
         * Retrieves an application by its user document number.
         * This method handles a GET request to find an application using its user
         * document number.
         * It returns a reactive Mono containing the response entity.
         * 
         * @param userDocumentNumber
         * @return
         */
        @Operation(summary = "Buscar una solicitud por su documento", description = "Recibe un número de documento (String) para buscarla.", tags = {"Solicitudes"}, responses = {
                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
        })
        @GetMapping("/buscar")
        public Mono<ResponseEntity<MessageResponse<List<Application>>>> getApplicationByUserDocumentNumber(
                        @RequestParam("user_document_number") String userDocumentNumber) {
                return applicationUseCase.getApplicationByUserDocumentNumber(userDocumentNumber)
                                .collectList()
                                .flatMap(applicationRetrieved -> {
                                        if (applicationRetrieved.isEmpty()) {
                                                return Mono.error(new ApplicationNotFoundException(
                                                                "Solicitudes con el documento " + userDocumentNumber
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
        @Operation(summary = "Crear una nueva solicitud", description = "Recibe un objeto ApplicationRequest para crear una nueva solicitud.", tags = {"Solicitudes"}, responses = {
                        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
        })
        @PostMapping()
        public Mono<ResponseEntity<MessageResponse<ApplicationResponse>>> createApplication(
                        @RequestBody ApplicationRequest applicationRequest) {
                final ApplicationCreditType applicationCreditType = new ApplicationCreditType(
                        UUID.fromString(applicationRequest.creditType()),
                        null,
                        null,
                        null,
                        BigDecimal.ZERO,
                        false);
                final Application application = new Application(
                        null,
                        applicationRequest.userDocumentNumber(),
                        applicationRequest.amount(),
                        applicationRequest.creditPeriod(),
                        null,
                        applicationCreditType);
                // Validate if the user exists
                return authService.validateUserExists(application.userDocumentNumber())
                                .flatMap(user -> Mono.just(application)
                                                .flatMap(validatedApplication -> applicationUseCase
                                                                .createApplication(validatedApplication)
                                                                .map(createdApplication -> ResponseEntity
                                                                                .status(HttpStatus.CREATED)
                                                                                .body(MessageResponse
                                                                                                .<ApplicationResponse>builder()
                                                                                                .message("Solicitud creada")
                                                                                                .data(new ApplicationResponse(
                                                                                                        createdApplication.id(),
                                                                                                        createdApplication.userDocumentNumber(),
                                                                                                        createdApplication.amount(),
                                                                                                        createdApplication.creditPeriod(),
                                                                                                        createdApplication.applicationCreditType() != null ? createdApplication.applicationCreditType().creditType() : null,
                                                                                                        createdApplication.applicationCreditType() != null ? createdApplication.applicationCreditType().interestRate() : null,
                                                                                                        createdApplication.applicationStatus() != null ? createdApplication.applicationStatus().description() : null
                                                                                                ))
                                                                                                .build()))));
        }
}
