package co.com.wdgg.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtConfigurationProperties(
        String secretKey,
        Long expirationTime
) {
}
