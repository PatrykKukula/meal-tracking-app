package io.github.patrykkukula.product_ms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.patrykkukula.product_ms.constants.ProductCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Data
public class ProductDto {
    private Long productId;

    @NotEmpty(message = "Product name cannot empty")
    @Size(max = 64, message = "Product name cannot exceed 64 characters")
    private String name;

    @NotNull(message = "Product category must be valid option")
    private ProductCategory productCategory;

    @PositiveOrZero(message = "Calories cannot be less than 0")
    @NotNull(message = "Calories is required")
    private Integer calories;

    @PositiveOrZero(message = "Protein cannot be less than 0")
    @NotNull(message = "Protein is required")
    private Integer protein;

    @PositiveOrZero(message = "Carbs cannot be less than 0")
    @NotNull(message = "Carbs are required")
    private Integer carbs;

    @PositiveOrZero(message = "Fat cannot be less than 0")
    @NotNull(message = "Fat is required")
    private Integer fat;

    private String ownerUsername;
}
