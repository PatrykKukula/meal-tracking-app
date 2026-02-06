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
    @JsonIgnore
    private Long productId;

    @NotEmpty(message = "Product name cannot be null or empty")
    @Size(max = 64, message = "Product name cannot exceed 64 characters")
    private String name;

    @NotNull(message = "Product category cannot be null")
    private ProductCategory productCategory;

    @PositiveOrZero(message = "Calories cannot be less than 0")
    @NotNull(message = "Calories cannot be null")
    private Integer calories;

    @PositiveOrZero(message = "Protein cannot be less than 0")
    @NotNull(message = "Protein cannot be null")
    private Integer protein;

    @PositiveOrZero(message = "Carbs cannot be less than 0")
    @NotNull(message = "Carbs cannot be null")
    private Integer carbs;

    @PositiveOrZero(message = "Fat cannot be less than 0")
    @NotNull(message = "Fat cannot be null")
    private Integer fat;

    @JsonIgnore
    private String ownerUsername;
}
