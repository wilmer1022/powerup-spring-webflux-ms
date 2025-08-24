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


@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {

    private final UserUseCase userUseCase;
    private final UserValidator userValidator;

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
