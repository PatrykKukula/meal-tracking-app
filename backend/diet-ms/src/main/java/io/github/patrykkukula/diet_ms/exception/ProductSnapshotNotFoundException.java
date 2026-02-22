package io.github.patrykkukula.diet_ms.exception;

// This should not be thrown if application is designed properly
public class ProductSnapshotNotFoundException extends RuntimeException {
    public ProductSnapshotNotFoundException(Long productId) {
        super("Product snapshot with ID %s not found".formatted(productId));
    }
}
