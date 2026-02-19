package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;

public class ProductQuantityMapper {
    private ProductQuantityMapper() {}

    public static ProductQuantityDto mapProductQuantityToProductQuantityDto(ProductQuantity productQuantity) {
        ProductQuantityDto productQuantityDto = new ProductQuantityDto();
        productQuantityDto.setQuantity(productQuantity.getQuantity());
        return productQuantityDto;
    }
}
