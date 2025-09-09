package co.com.wdgg.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webclient.client")
public record WebCleintProperties(
        String host,
        String port) {
}
