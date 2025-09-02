package co.com.wdgg.api.middleware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.wdgg.api.services.JwtService;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.core.io.buffer.DataBufferUtils;

@Component
public class OwnerEmailWebFilter implements WebFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public OwnerEmailWebFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (!path.equals("/api/v1/solicitud") && !exchange.getRequest().getMethod().equals(HttpMethod.POST)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange);
        }

        String jwt = authorizationHeader.substring(7);
        String username = jwtService.extractUsername(jwt);

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return processAndForwardRequest(exchange, chain, bytes, username);
                });
    }

    private Mono<Void> processAndForwardRequest(ServerWebExchange exchange, WebFilterChain chain, byte[] bytes,
            String username) {
        try {
            String requestBody = new String(bytes, StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            String requestedUserEmail = jsonNode.has("userEmail") ? jsonNode.get("userEmail").asText() : null;

            if (requestedUserEmail != null && !Objects.equals(requestedUserEmail, username)) {
                return forbiddenResponse(exchange, "Acceso denegado. El recurso no te pertenece.");
            }

            Flux<DataBuffer> cachedBody = Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
            ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public @NonNull Flux<DataBuffer> getBody() {
                    return cachedBody;
                }
            };
            return chain.filter(exchange.mutate().request(decoratedRequest).build());

        } catch (JsonProcessingException e) {
            return forbiddenResponse(exchange, "Petición no válida.");
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return writeJsonResponse(exchange, "{\"message\":\"Token JWT no válido.\"}");
    }

    private Mono<Void> forbiddenResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return writeJsonResponse(exchange, "{\"message\":\"" + message + "\"}");
    }

    private Mono<Void> writeJsonResponse(ServerWebExchange exchange, String json) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))));
    }
}