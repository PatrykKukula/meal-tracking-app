package io.github.patrykkukula.product_ms.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Product with ID %s not found".formatted(productId));
    }
}
