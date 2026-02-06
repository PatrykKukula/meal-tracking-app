package io.github.patrykkukula.product_ms.exception;

public class InvalidProductCategoryException extends RuntimeException {
    public InvalidProductCategoryException() {
        super("Product category cannot be set to ALL - it is for filtering purpose only");
    }
}
