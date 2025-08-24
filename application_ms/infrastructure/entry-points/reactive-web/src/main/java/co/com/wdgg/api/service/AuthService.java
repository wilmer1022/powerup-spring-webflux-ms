package co.com.wdgg.api.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import co.com.wdgg.api.exceptions.UserNotFoundException;
import co.com.wdgg.model.user.User;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    private final WebClient webClient;

    public AuthService(@Qualifier("authServiceWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<User> validateUserExists(String userDocumentNumber) {
        return webClient.get().uri("/buscar?document_number={document_number}", userDocumentNumber)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(new UserNotFoundException(
                        "Usuario con documento " + userDocumentNumber + " no encontrado")))
                .bodyToMono(User.class);
    }
}