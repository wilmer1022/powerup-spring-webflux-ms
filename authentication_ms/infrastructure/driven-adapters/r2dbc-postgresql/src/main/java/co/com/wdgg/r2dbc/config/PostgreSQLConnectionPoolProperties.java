package co.com.wdgg.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.r2dbc.pool")
public record PostgreSQLConnectionPoolProperties(
        int initialSize,
        int maxSize,
        int maxIdleTime) {
}
