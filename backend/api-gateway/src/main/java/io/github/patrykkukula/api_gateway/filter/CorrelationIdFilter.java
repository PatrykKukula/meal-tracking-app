package io.github.patrykkukula.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CorrelationIdFilter implements GlobalFilter {
    private final String CORRELATION_ID = "correlation-id";
    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    /**
     * Process the Web request and (optionally) delegate to the next {@code GatewayFilter}
     * through the given {@link GatewayFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = UUID.randomUUID().toString();

        log.info("Generated correlationId for request header:{} ", correlationId);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder
                        .headers(headers -> headers.set(CORRELATION_ID, correlationId))
                        .build())
                .build();

        MDC.put(CORRELATION_ID, correlationId);

        return chain.filter(mutatedExchange);
    }
}
