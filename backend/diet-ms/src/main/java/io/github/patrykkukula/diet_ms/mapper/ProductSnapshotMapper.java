package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.dto.ProductDtoRead;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.mealtrackingapp_common.events.ProductUpdatedEvent;

public class ProductSnapshotMapper {
    private ProductSnapshotMapper(){}

    public static ProductSnapshot mapProductUpdatedEventToSnapshotUpdate(ProductUpdatedEvent event, ProductSnapshot productSnapshot) {
        productSnapshot.setName(event.name());
        productSnapshot.setProductCategory(ProductCategory.valueOf(event.productCategory()));
        productSnapshot.setCalories(event.calories());
        productSnapshot.setProtein(event.protein());
        productSnapshot.setCarbs(event.carbs());
        productSnapshot.setFat(event.fat());
        return productSnapshot;
    }

    public static ProductDtoRead mapProductSnapshotToProductDtoRead(ProductSnapshot snapshot, ProductQuantity quantity) {
        return new ProductDtoRead(
                snapshot.getProductId(),
                quantity.getProductQuantityId(),
                quantity.getQuantity(),
                snapshot.getName(),
                snapshot.getProductCategory(),
                snapshot.getCalories(),
                snapshot.getProtein(),
                snapshot.getCarbs(),
                snapshot.getFat()
        );
    }
}
