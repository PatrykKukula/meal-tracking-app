package io.github.patrykkukula.diet_ms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record DietDayDtoRead(Long dietDayId, String ownerUsername, @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date, List<MealDtoRead> meals) {}
