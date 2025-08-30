package co.com.wdgg.api.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Configuration
public class JwtConfiguration {

    private final JwtConfigurationProperties jwtConfigurationProperties;

    public JwtConfiguration(JwtConfigurationProperties jwtConfigurationProperties) {
        this.jwtConfigurationProperties = jwtConfigurationProperties;
    }

    @Bean
    Mono<SecretKey> secretKeyMono() {
        return Mono.fromCallable(() -> {
            try {
                Path keyFilePath = Path.of(jwtConfigurationProperties.secretKey());
                if (!Files.exists(keyFilePath)) {
                    throw new IllegalArgumentException("El archivo de clave no existe.");
                }

                byte[] keyBytes = Files.readAllBytes(keyFilePath);
                if (keyBytes.length != 64) {
                    throw new IllegalArgumentException("La longitud de la clave debe ser de 64 bytes/512 bits.");
                }
                return Keys.hmacShaKeyFor(keyBytes);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo cargar la clave de cifrado.", e);
            }
        });
    }
}
