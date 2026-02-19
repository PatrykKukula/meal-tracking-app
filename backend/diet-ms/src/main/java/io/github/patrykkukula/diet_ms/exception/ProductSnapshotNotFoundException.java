package io.github.patrykkukula.diet_ms.exception;

public class ProductSnapshotNotFoundException extends RuntimeException {
    public ProductSnapshotNotFoundException(Long productId) {
        super("Product snapshot with ID %s not found".formatted(productId));
    }
}
