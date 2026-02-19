package io.github.patrykkukula.diet_ms.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MealDto {
    @Size(max = 32, message = "Meal name is too long! It can be up to 32 characters long.")
    private String name;

    @NotEmpty(message = "Meal must have at least one product")
    private List<ProductQuantityDto> quantities;
}
