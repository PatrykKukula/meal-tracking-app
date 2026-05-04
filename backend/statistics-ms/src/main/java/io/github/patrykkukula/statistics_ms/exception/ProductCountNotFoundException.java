package io.github.patrykkukula.statistics_ms.exception;

public class ProductCountNotFoundException extends RuntimeException {
    public ProductCountNotFoundException(Long productId, String username) {
        super("ProductCount not found for productId: %s and user: %s".formatted(productId, username));
    }
}
