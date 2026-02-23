package io.github.patrykkukula.diet_ms.exception;

// This should not be thrown if application is designed properly
public class MealNotFoundException extends RuntimeException {
    public MealNotFoundException(Long id) {
        super("Meal with ID %s not found".formatted(id));
    }
}
