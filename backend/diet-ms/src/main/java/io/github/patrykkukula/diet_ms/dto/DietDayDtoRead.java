package io.github.patrykkukula.diet_ms.dto;

import java.util.List;

public record DietDayDtoRead(Long dietDayId, String ownerUsername, List<MealDtoRead> meals) {}
