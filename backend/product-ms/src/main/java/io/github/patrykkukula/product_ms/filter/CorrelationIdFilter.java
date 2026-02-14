package io.github.patrykkukula.product_ms.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.servlet.filter.OrderedFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;
import static io.github.patrykkukula.product_ms.constants.GlobalConstants.CORRELATION_ID;

/*
 * Filter to add correlation-id to header and to log entering to and leaving from request
 */
@Component
@Order(OrderedFilter.HIGHEST_PRECEDENCE)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            String correlationId = request.getHeader(CORRELATION_ID);
            log.info("correlationId found in request header: {}", correlationId);

            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
                log.info("correlationId not found in request header. Generated correlationId: {} ", correlationId);
            }

            MDC.put(CORRELATION_ID, correlationId);             // set MDC entry to track logs

            log.info("Incoming request - method:{} | path:{}",
                    request.getMethod(),
                    request.getRequestURI());

            response.setHeader(CORRELATION_ID, correlationId);

            filterChain.doFilter(request, response);
        } finally {
            log.info("Request completed - method:{} | path:{} | Status:{} | durationMs:{}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    System.currentTimeMillis() - startTime
            );
            MDC.clear();
        }
    }

    // enable MDC in GlobalExceptionHandler logs
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
