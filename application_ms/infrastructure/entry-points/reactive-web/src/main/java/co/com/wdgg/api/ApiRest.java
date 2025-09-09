package co.com.wdgg.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.wdgg.api.dto.ApplicationMapper;
import co.com.wdgg.api.dto.ApplicationRequest;
import co.com.wdgg.api.dto.ApplicationResponse;
import co.com.wdgg.api.dto.ApplicationReviewableResponse;
import co.com.wdgg.api.dto.MessageResponse;
import co.com.wdgg.api.exceptions.ApplicationNotFoundException;
import co.com.wdgg.api.exceptions.UserNotFoundException;
import co.com.wdgg.api.services.AuthService;
import co.com.wdgg.model.application.Application;
import co.com.wdgg.model.user.User;
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

        public ApiRest(ApplicationUseCase applicationUseCase, ApplicationMapper applicationMapper,
                        AuthService authService) {
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
        @GetMapping("/{id}")
        public Mono<ResponseEntity<MessageResponse<ApplicationResponse>>> getApplicationById(
                        @Schema(description = "Identificador único de la solicitud") @PathVariable("id") String id) {
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
        @GetMapping("/buscar/{user_email}")
        public Mono<ResponseEntity<MessageResponse<List<ApplicationResponse>>>> getApplicationByUserEmail(
                        @Schema(description = "Correo electrónico del usuario", format = "email") @PathVariable("user_email") String userEmail) {
                return applicationUseCase.getApplicationByUserEmail(userEmail)
                                .collectList()
                                .flatMap(applicationRetrievedList -> {
                                        if (applicationRetrievedList.isEmpty()) {
                                                return Mono.error(new ApplicationNotFoundException(
                                                                "Solicitudes con el correo electrónico " + userEmail
                                                                                + " no encontradas"));
                                        }

                                        List<ApplicationResponse> responseList = applicationRetrievedList.stream()
                                                        .map(applicationMapper::toResponse)
                                                        .toList();

                                        return Mono.just(ResponseEntity.status(HttpStatus.OK)
                                                        .body(MessageResponse.<List<ApplicationResponse>>builder()
                                                                        .message("Solicitudes encontradas")
                                                                        .data(responseList)
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

        /**
         * Retrieves reviewsable applications by pagination.
         * This method handles a GET request to retrieve reviewsable applications by
         * pagination.
         * It returns a reactive Mono containing the response entity.
         * 
         * @param page
         * @param size
         * @return
         */
        @Operation(summary = "Buscar aplicaciones revisables", description = "Recibe un parametro tamaño (size) y otro pagina (page) para buscar las aplicaciones revisables.", tags = {
                        "Solicitudes" }, responses = {
                                        @ApiResponse(responseCode = "200", description = "Aplicaciones revisables encontradas"),
                                        @ApiResponse(responseCode = "400", description = "Error de validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
                                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
                        })
        @GetMapping("/buscar/aplicaciones-revisables")
        public Mono<List<ApplicationReviewableResponse>> getReviewableApplications(
                        @Schema(description = "Pagina actual") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Schema(description = "Tamaño de la página") @RequestParam(name = "size", defaultValue = "10") int size,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

                return applicationUseCase.getReviewableApplications(page, size)
                                .collectList()
                                .flatMap(applications -> {

                                        if (applications.isEmpty()) {
                                                return Mono.error(new ApplicationNotFoundException("Aplicaciones revisables no encontradas"));
                                        }

                                        List<String> userEmails = applications.stream()
                                                        .map(Application::userEmail)
                                                        .toList();

                                        return authService.getUsersByEmails(userEmails, authorizationHeader)
                                                        .collectList()
                                                        .map(users -> {
                                                                if (users.isEmpty()) {
                                                                        Mono.error(new UserNotFoundException("No se encontraron usuarios"));
                                                                }
                                                                Map<String, User> userMap = users.stream()
                                                                                .collect(Collectors.toMap(User::email,
                                                                                                user -> user));

                                                                return applications.stream()
                                                                                .map(application -> {
                                                                                        User user = userMap.get(
                                                                                                        application.userEmail());
                                                                                        BigDecimal monthlyRequestAmount = calculateMonthlyRequestAmount(
                                                                                                        application.amount(),
                                                                                                        application.applicationCreditType()
                                                                                                                        .interestRate());

                                                                                        return new ApplicationReviewableResponse(
                                                                                                        application.id(),
                                                                                                        application.userEmail(),
                                                                                                        user.firstName(),
                                                                                                        user.lastName(),
                                                                                                        application.amount(),
                                                                                                        application.creditPeriod(),
                                                                                                        application.applicationCreditType()
                                                                                                                        .creditType(),
                                                                                                        application.applicationCreditType()
                                                                                                                        .interestRate(),
                                                                                                        application.applicationStatus()
                                                                                                                        .status(),
                                                                                                        user.salary(),
                                                                                                        monthlyRequestAmount);
                                                                                }).toList();
                                                        })
                                                        .onErrorResume(e -> Mono.error(new UserNotFoundException("Error al obtener usuarios por emails")));
                                })
                                .onErrorResume(e -> Mono.error(new ApplicationNotFoundException(e.getMessage())));
        }

        private BigDecimal calculateMonthlyRequestAmount(BigDecimal amount, BigDecimal interestRate) {
                BigDecimal interestAmount = amount.multiply(BigDecimal.valueOf(interestRate.longValue()));
                return amount.add(interestAmount).divide(BigDecimal.valueOf(36), 2, RoundingMode.HALF_DOWN);
        }

}
