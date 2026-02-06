package io.github.patrykkukula.product_ms.exception;

public class CustomProductAmountExceededException extends RuntimeException {
    public CustomProductAmountExceededException() {
        super("You can add at most 100 custom products. Please remove other product to create space for new one.");
    }
}
