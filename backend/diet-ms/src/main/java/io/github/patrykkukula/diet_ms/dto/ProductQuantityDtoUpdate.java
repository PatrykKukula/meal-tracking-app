package io.github.patrykkukula.diet_ms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductQuantityDtoUpdate {
    @Positive(message = "Product quantity must be greater than 0")
    @NotNull(message = "Specify product quantity")
    private double quantity;
}
