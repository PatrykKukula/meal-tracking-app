package io.github.patrykkukula.diet_ms.dto;

import java.util.List;

public record MealDtoRead(Long mealId, String name, List<ProductDtoRead> products) {}
