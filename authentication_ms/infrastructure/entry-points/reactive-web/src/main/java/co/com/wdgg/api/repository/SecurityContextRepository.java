package co.com.wdgg.api.repository;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import co.com.wdgg.api.services.JwtAuthenticationManager;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager authenticationManager;
    
    public SecurityContextRepository(JwtAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith("Bearer "))
                .flatMap(header -> {
                    String authToken = header.substring(7);
                    return authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(authToken, authToken)
                    ).onErrorResume(e -> {
                        if (e instanceof JwtException) {
                            return Mono.empty();
                        }
                        return Mono.error(e);
                    }).map(SecurityContextImpl::new);
                });
    }
}
