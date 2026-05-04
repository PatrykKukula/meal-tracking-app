package io.github.patrykkukula.statistics_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor @Builder
public class ProductCountDto {
    private Long productCountId;
    private String productName;
    private Long productId;
    private String username;
    private Integer usageCount;
    private Double totalQuantity;
    private Double averageQuantity;
}
