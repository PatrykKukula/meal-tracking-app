package io.github.patrykkukula.diet_ms.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductQuantityDto {
    @PositiveOrZero(message = "ProductId must be positive value")
    @NotNull(message = "ProductId cannot be empty")
    private Long productId;

    @Positive(message = "Product quantity must be positive value")
    @NotNull(message = "Specify product quantity")
    private double quantity;
}
