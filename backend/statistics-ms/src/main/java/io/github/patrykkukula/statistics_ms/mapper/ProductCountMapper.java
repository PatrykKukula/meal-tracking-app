package io.github.patrykkukula.statistics_ms.mapper;

import io.github.patrykkukula.statistics_ms.dto.ProductCountDto;
import io.github.patrykkukula.statistics_ms.model.ProductCount;

public class ProductCountMapper {

    public static ProductCountDto mapProductCountToProductCountDto(ProductCount productCount) {
        return ProductCountDto.builder()
                .productCountId(productCount.getProductCountId())
                .productName(productCount.getProductName())
                .productId(productCount.getProductId())
                .username(productCount.getUsername())
                .usageCount(productCount.getUsageCount())
                .totalQuantity(productCount.getTotalQuantity())
                .averageQuantity(calculateAverageQuantity(productCount))
                .build();
    }

    private static double calculateAverageQuantity(ProductCount productCount) {
        return productCount.getTotalQuantity() / productCount.getUsageCount();
    }
}
