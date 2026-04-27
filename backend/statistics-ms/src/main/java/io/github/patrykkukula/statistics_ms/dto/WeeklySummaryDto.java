package io.github.patrykkukula.statistics_ms.dto;

public record WeeklySummaryDto(Long weeklySummaryId,
                               Integer totalCalories,
                               Integer averageCalories,
                               Integer totalProtein,
                               Integer daysLogged) {
}
