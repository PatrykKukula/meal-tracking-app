package io.github.patrykkukula.diet_ms.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductQuantityDto {
    @PositiveOrZero(message = "ProductId must be positive value")
    @NotEmpty(message = "ProductId cannot be empty")
    private Long productId;

    @Positive(message = "Specify product quantity")
    @NotEmpty(message = "Specify product quantity")
    private double quantity;
}
