package co.com.wdgg.api.services;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import reactor.core.publisher.Mono;

import java.security.SignatureException;
import java.util.Collections;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.just(token)
                .filter(jwtService::validateToken)
                .onErrorResume(SignatureException.class,
                        e -> Mono.error(new JwtException("Firma del token inválida", e)))
                .onErrorResume(MalformedJwtException.class,
                        e -> Mono.error(new JwtException("Formato de token inválido", e)))
                .onErrorResume(ExpiredJwtException.class,
                        e -> Mono.error(new JwtException("Token expirado", e)))
                .map(jwtService::getClaims)
                .map(claims -> {
                    String username = claims.getSubject();
                    String role = (String) claims.get("role");
                    return new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                })
                .cast(Authentication.class);
    }
}
