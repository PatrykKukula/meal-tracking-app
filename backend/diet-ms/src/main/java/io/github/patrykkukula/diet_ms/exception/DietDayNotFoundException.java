package io.github.patrykkukula.diet_ms.exception;

// This should not be thrown if application is designed properly
public class DietDayNotFoundException extends RuntimeException {
    public DietDayNotFoundException(Long id) {
        super("DietDay with ID %s not found".formatted(id));
    }
}
