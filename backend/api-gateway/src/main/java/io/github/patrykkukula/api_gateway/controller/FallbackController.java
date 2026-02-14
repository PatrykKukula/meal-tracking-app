package io.github.patrykkukula.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/api/fallback")
    public ResponseEntity<Mono<String>> fallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .body(Mono.just("Unexpected error occurred. Please try again later or contact support."));
    }
}
