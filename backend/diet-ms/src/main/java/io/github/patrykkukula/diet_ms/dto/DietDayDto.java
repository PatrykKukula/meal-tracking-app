package io.github.patrykkukula.diet_ms.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DietDayDto {
    private Long dietDayId;
    @FutureOrPresent(message = "You cannot add diet to the past day")
    @NotNull(message = "Date cannot be empty")
    private LocalDate date;

    @NotEmpty(message = "Diet day must contains at least one meal")
    private List<MealDto> meals;
}
