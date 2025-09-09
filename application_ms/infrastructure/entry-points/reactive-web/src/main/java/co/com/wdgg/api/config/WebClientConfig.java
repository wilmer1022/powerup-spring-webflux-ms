package co.com.wdgg.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final WebCleintProperties webClientProperties;

    public WebClientConfig(WebCleintProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
    }

    @Bean
    WebClient authServiceWebClient() {
        return WebClient.builder()
                .baseUrl("http://" + webClientProperties.host() + ":" + webClientProperties.port() + "/api/v1/usuarios")
                .build();
    }
}
