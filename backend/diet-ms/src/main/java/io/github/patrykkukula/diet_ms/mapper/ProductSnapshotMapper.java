package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.events.ProductUpdatedEvent;

public class ProductSnapshotMapper {
    private ProductSnapshotMapper(){}

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
