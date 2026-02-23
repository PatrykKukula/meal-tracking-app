package io.github.patrykkukula.diet_ms.exception;

// This should not be thrown if application is designed properly
public class ProductQuantityNotFoundException extends RuntimeException {
    public ProductQuantityNotFoundException(Long id) {
        super("ProductQuantity with ID %s not found".formatted(id));
    }
}
