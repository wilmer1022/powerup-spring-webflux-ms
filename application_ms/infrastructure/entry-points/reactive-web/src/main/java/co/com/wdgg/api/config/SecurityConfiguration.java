package co.com.wdgg.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import co.com.wdgg.api.repository.SecurityContextRepository;
import co.com.wdgg.api.services.JwtAuthenticationManager;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

        private final JwtAuthenticationManager jwtAuthenticationManager;
        private final SecurityContextRepository securityContextRepository;

        public SecurityConfiguration(JwtAuthenticationManager jwtAuthenticationManager,
                        SecurityContextRepository securityContextRepository) {
                this.jwtAuthenticationManager = jwtAuthenticationManager;
                this.securityContextRepository = securityContextRepository;
        }

        @Bean
        SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                                .authenticationManager(jwtAuthenticationManager)
                                .securityContextRepository(securityContextRepository)
                                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                                                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
                                                        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                                        swe.getResponse().getHeaders()
                                                                        .setContentType(MediaType.APPLICATION_JSON);
                                                        String responseBody = "{\"message\": \"Token invalido" + "\"}";
                                                        swe.getResponse().writeWith(Mono.just(swe.getResponse()
                                                                        .bufferFactory().wrap(responseBody.getBytes())))
                                                                        .subscribe();
                                                }))
                                                .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> {
                                                        swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                                        swe.getResponse().getHeaders()
                                                                        .setContentType(MediaType.APPLICATION_JSON);
                                                        String responseBody = "{\"message\": \"Acceso Denegado" + "\"}";
                                                        swe.getResponse().writeWith(Mono.just(swe.getResponse()
                                                                        .bufferFactory().wrap(responseBody.getBytes())))
                                                                        .subscribe();
                                                })))
                                .authorizeExchange(exchanges -> exchanges
                                                .pathMatchers("/swagger-ui.html", "/swagger-ui/**",
                                                                "/swagger-resources/**", "/v3/api-docs/**")
                                                .permitAll()
                                                .pathMatchers(HttpMethod.POST, "/api/v1/solicitud")
                                                .hasRole("CLIENTE")
                                                .pathMatchers(HttpMethod.GET, "/api/v1/solicitud/buscar/aplicaciones-revisables**")
                                                .hasRole("ASESOR")
                                                .anyExchange().authenticated())
                                .build();
        }
}
