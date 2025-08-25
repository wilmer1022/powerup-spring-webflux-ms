package co.com.wdgg.api;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.wdgg.api.dto.MessageResponse;
import co.com.wdgg.api.exceptions.ApplicationNotFoundException;
import co.com.wdgg.api.service.AuthService;
import co.com.wdgg.api.validators.ApplicationValidator;
import co.com.wdgg.model.application.Application;
import co.com.wdgg.usecase.application.ApplicationUseCase;
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
        private final ApplicationValidator applicationValidator;
        private final AuthService authService;

        /**
         * Retrieves an application by its unique ID.
         * This method handles a GET request to find an application using its ID.
         * It returns a reactive Mono containing the response entity.
         * @param id
         * @return
         */
        @GetMapping()
        public Mono<ResponseEntity<MessageResponse<Application>>> getApplicationById(@RequestParam("id") String id) {
                return applicationUseCase.getApplicationById(id)
                                .map(applicationRetrieved -> ResponseEntity.status(HttpStatus.OK)
                                                .body(MessageResponse.<Application>builder()
                                                                .message("Solicitud encontrada")
                                                                .data(applicationRetrieved)
                                                                .build()))
                                .switchIfEmpty(Mono.error(new ApplicationNotFoundException(
                                                "Solicitud con id " + id + " no encontrada")));
        }

        /**
         * Retrieves an application by its user document number.
         * This method handles a GET request to find an application using its user document number.
         * It returns a reactive Mono containing the response entity.
         * @param userDocumentNumber
         * @return
         */
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
         * @param application
         * @return
         */
        @PostMapping()
        public Mono<ResponseEntity<MessageResponse<Application>>> createApplication(
                        @RequestBody Application application) {
                // Validate if the user exists and if the application is valid
                return authService.validateUserExists(application.userDocumentNumber())
                                .flatMap(user -> Mono.just(application)
                                                .doOnNext(a -> applicationValidator.validate(application,
                                                                new BeanPropertyBindingResult(application,
                                                                                "application")))
                                                .flatMap(validatedApplication -> applicationUseCase
                                                                .createApplication(validatedApplication)
                                                                .map(createdApplication -> ResponseEntity
                                                                                .status(HttpStatus.CREATED)
                                                                                .body(MessageResponse
                                                                                                .<Application>builder()
                                                                                                .message("Solicitud creada")
                                                                                                .data(createdApplication)
                                                                                                .build()))))
                                .onErrorResume(IllegalArgumentException.class,
                                                ex -> Mono.error(new IllegalArgumentException(ex.getMessage())));
        }
}
