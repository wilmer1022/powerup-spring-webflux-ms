package co.com.wdgg.api.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import co.com.wdgg.api.dto.MessageResponse;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleAllExceptions(Exception ex) {
        logger.error("Unexpected exception [{}]", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MessageResponse.<String>builder()
                .message("Ha ocurrido un error inesperado!")
                .data(null)
                .build()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument [{}]", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageResponse.<String>builder()
                .message(ex.getMessage())
                .data(null)
                .build()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<MessageResponse<String>>> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found [{}]", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageResponse.<String>builder()
                .message("El usuario no se encuentra!")
                .data(null)
                .build()));
    }
}
