package co.com.wdgg.api;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.com.wdgg.api.dto.MessageResponse;
import co.com.wdgg.api.exceptions.ApplicationNotFoundException;
import co.com.wdgg.model.application.Application;
import co.com.wdgg.usecase.application.ApplicationUseCase;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {

    private final ApplicationUseCase applicationUseCase;

    @GetMapping()
    public Mono<ResponseEntity<MessageResponse<Application>>> getApplicationById(@RequestParam("id") String id) {
        return applicationUseCase.getApplicationById(id)
                .map(applicationRetrieved -> ResponseEntity.status(HttpStatus.OK)
                        .body(MessageResponse.<Application>builder()
                                .message("Solicitud encontrada")
                                .data(applicationRetrieved)
                                .build()))
                .switchIfEmpty(Mono.error(new ApplicationNotFoundException("Solicitud con id " + id + " no encontrada")));
    }

    @GetMapping("/buscar")
    public Mono<ResponseEntity<MessageResponse<List<Application>>>> getApplicationByUserDocumentNumber(@RequestParam("user_document_number") String userDocumentNumber) {
        return applicationUseCase.getApplicationByUserDocumentNumber(userDocumentNumber)
                .collectList()
                .map(applicationRetrieved -> ResponseEntity.status(HttpStatus.OK)
                        .body(MessageResponse.<List<Application>>builder()
                                .message("Solicitud encontrada")
                                .data(applicationRetrieved)
                                .build()));
    }

    @PostMapping()
    public Mono<ResponseEntity<MessageResponse<Application>>> createApplication(@RequestBody Application application) {
        return applicationUseCase.createApplication(application)
                .map(createdApplication -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(MessageResponse.<Application>builder()
                                .message("Solicitud creada")
                                .data(createdApplication)
                                .build()));
    }
}
