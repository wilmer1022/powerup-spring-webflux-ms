package co.com.wdgg.api.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

@Configuration
public class WebConfig {

    private final WebFilter ownerEmailWebFilter;

    public WebConfig(@Qualifier("ownerEmailWebFilter") WebFilter ownerEmailWebFilter) {
        this.ownerEmailWebFilter = ownerEmailWebFilter;
    }

    @Bean
    WebFilter filterForSpecificPaths() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (path.equals("/api/v1/solicitud") && exchange.getRequest().getMethod().equals(HttpMethod.POST)) {
                return ownerEmailWebFilter.filter(exchange, chain);
            }
            return chain.filter(exchange);
        };
    }
}
