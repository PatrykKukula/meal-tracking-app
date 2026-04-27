package io.github.patrykkukula.statistics_ms.dto;

public record DailyAverageDto(Long dailyAverageId,
                              Integer averageCalories,
                              Integer averageProtein,
                              Integer averageCarbs,
                              Integer averageFat,
                              Integer totalDaysWithDied) {
}
