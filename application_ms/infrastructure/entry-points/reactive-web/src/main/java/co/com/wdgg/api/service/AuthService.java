package co.com.wdgg.api.service;

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

    /**
     * Validates if a user with the given document number exists in the authentication microservice.
     * This method sends a GET request to the authentication microservice to check if the user exists.
     * If the user is found, it returns the user object. If the user is not found, it throws an exception.
     * @param userDocumentNumber
     * @return the user object if found
     */
    public Mono<User> validateUserExists(String userDocumentNumber) {
        return webClient.get().uri("/buscar?document_number={document_number}", userDocumentNumber)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(new UserNotFoundException(
                        "Usuario con documento " + userDocumentNumber + " no encontrado")))
                .bodyToMono(User.class);
    }
}