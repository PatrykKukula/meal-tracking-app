package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.dto.ProductDto;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;

public class ProductSnapshotMapper {
    private ProductSnapshotMapper(){}

    public static ProductSnapshot mapProductDtoToSnapshot(ProductDto productDto) {
        return new ProductSnapshot(
                productDto.productId(),
                productDto.name(),
                productDto.productCategory(),
                productDto.calories(),
                productDto.protein(),
                productDto.carbs(),
                productDto.fat(),
                productDto.ownerUsername() != null ? productDto.ownerUsername() : null
        );
    }

    public static ProductSnapshot mapProductDtoToSnapshotUpdate(ProductDto productDto, ProductSnapshot productSnapshot) {
        productSnapshot.setName(productDto.name());
        productSnapshot.setProductCategory(productDto.productCategory());
        productSnapshot.setCalories(productDto.calories());
        productSnapshot.setProtein(productDto.protein());
        productSnapshot.setCarbs(productDto.carbs());
        productSnapshot.setFat(productDto.fat());
        return productSnapshot;
    }
}
