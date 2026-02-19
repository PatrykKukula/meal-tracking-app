package io.github.patrykkukula.diet_ms.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DietDayDto {
    private Long dietDatId;
    @FutureOrPresent(message = "You cannot add diet to the past day")
    @NotEmpty(message = "Date cannot be empty")
    private LocalDate date;

    @NotEmpty(message = "Diet day must contains at least one meal")
    private List<MealDto> meals;
}
