package io.github.patrykkukula.diet_ms.builder;

import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;

public class ProductQuantityDtoTestBuilder {
    private Long id = 1L;
    private Double quantity = 5.0;

    private ProductQuantityDtoTestBuilder() {}

    public static ProductQuantityDtoTestBuilder productQuantityDto() {
        return new ProductQuantityDtoTestBuilder();
    }

    public ProductQuantityDtoTestBuilder quantity(Double quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductQuantityDto build() {
        ProductQuantityDto productQuantityDto = new ProductQuantityDto();
        productQuantityDto.setProductId(id);
        productQuantityDto.setQuantity(quantity);
        return productQuantityDto;
    }
}
