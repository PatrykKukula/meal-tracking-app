package io.github.patrykkukula.diet_ms.dto;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;

public record ProductDtoRead(Long productId,
                             Long productQuantityId,
                             Double quantity,
                             String name,
                             ProductCategory productCategory,
                             Integer calories,
                             Integer protein,
                             Integer carbs,
                             Integer fat) {
}
