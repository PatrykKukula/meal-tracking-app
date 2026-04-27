package io.github.patrykkukula.statistics_ms.dto;

import io.github.patrykkukula.statistics_ms.constants.ProductCategory;

public record MostUsedProductDto(Long mostUsedProductId,
                                 String productName,
                                 ProductCategory productCategory,
                                 Integer usageCount,
                                 Double totalQuantity,
                                 Double averageQuantity) {
}
