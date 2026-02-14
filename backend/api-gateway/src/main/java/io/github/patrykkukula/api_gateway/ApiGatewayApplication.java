package io.github.patrykkukula.api_gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(spec -> spec.path("/product/**")
                        .filters(fn -> fn.rewritePath("/product/?(?<remaining>.*)", "/${remaining}")
                                .circuitBreaker(
                                        cb -> cb.setName("default-cb").setFallbackUri("forward:/api/fallback"))
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
                                        .setMethods(HttpMethod.GET))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()).setKeyResolver(userKeyResolver()))
                                .addResponseHeader("X-Response-Time", formatResponseTime()))
                        .uri("lb://PRODUCT"))
                .build();
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        RedisRateLimiter redisRateLimiter = new RedisRateLimiter(10, 20, 1);
        redisRateLimiter.setIncludeHeaders(true);
        return redisRateLimiter;
    }

    @Bean
    KeyResolver userKeyResolver(){
        return   exchange ->
            exchange.getPrincipal()
                    .map(principal -> {
                        log.info("[RATE LIMITER] Principal found in request: {}", principal.getName());
                        return  principal.getName();
                    })
                    .defaultIfEmpty("anonymous:" + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    @Bean
    String formatResponseTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }
}
