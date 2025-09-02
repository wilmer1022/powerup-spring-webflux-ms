package co.com.wdgg.api.exceptions;

import java.nio.file.AccessDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;

import co.com.wdgg.api.dto.MessageResponse;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler extends WebFluxResponseStatusExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleAllExceptions(Exception ex) {
        logger.error("Unexpected exception [{}]", ex.getMessage());

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado!", ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument [{}]", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found [{}]", ex.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, "Usuario no encontrado", ex);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleValidationExceptions(WebExchangeBindException ex) {
        logger.error("Validation exception [{}]", ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Errores de validación", ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied [{}]", ex.getMessage());

        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), ex);
    }

    @ExceptionHandler(JwtException.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleJwtException(JwtException ex) {
        logger.error("JWT exception [{}]", ex.getMessage());

        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
    }

    private Mono<ResponseEntity<MessageResponse<String>>> buildErrorResponse(
            HttpStatus status,
            String message,
            Throwable ex) {

        logger.error("Handling exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage());

        MessageResponse<String> response = MessageResponse.<String>builder()
                .message(message)
                .data(null)
                .build();

        return Mono.just(ResponseEntity.status(status).body(response));
    }
}
