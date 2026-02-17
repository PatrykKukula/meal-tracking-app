package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.dto.ProductSnapshotDto;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.events.ProductCreatedEvent;
import io.github.patrykkukula.events.ProductUpdatedEvent;

public class ProductSnapshotMapper {
    private ProductSnapshotMapper(){}

    public static ProductSnapshot mapProductCreatedEventToSnapshot(ProductCreatedEvent productCreatedEvent) {
        return new ProductSnapshot(
                productCreatedEvent.productId(),
                productCreatedEvent.name(),
                ProductCategory.valueOf(productCreatedEvent.productCategory()),
                productCreatedEvent.calories(),
                productCreatedEvent.protein(),
                productCreatedEvent.carbs(),
                productCreatedEvent.fat(),
                productCreatedEvent.ownerUsername() != null ? productCreatedEvent.ownerUsername() : null
        );
    }

    public static ProductSnapshot mapProductDtoToSnapshotUpdate(ProductUpdatedEvent event, ProductSnapshot productSnapshot) {
        productSnapshot.setName(event.name());
        productSnapshot.setProductCategory(ProductCategory.valueOf(event.productCategory()));
        productSnapshot.setCalories(event.calories());
        productSnapshot.setProtein(event.protein());
        productSnapshot.setCarbs(event.carbs());
        productSnapshot.setFat(event.fat());
        return productSnapshot;
    }
}
