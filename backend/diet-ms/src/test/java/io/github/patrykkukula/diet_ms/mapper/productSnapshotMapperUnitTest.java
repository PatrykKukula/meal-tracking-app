package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.dto.ProductDtoRead;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.mealtrackingapp_common.events.ProductUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class productSnapshotMapperUnitTest {
    private ProductUpdatedEvent productUpdatedEvent;
    private ProductSnapshot productSnapshot = new ProductSnapshot();
    private ProductQuantity productQuantity = new ProductQuantity();

    @BeforeEach
    public void setUp() {
        productUpdatedEvent = new ProductUpdatedEvent(
                1L,
                "updated",
                ProductCategory.FISH.name(),
                100,
                10,
                1,
                0,
                "user"
        );

        productSnapshot.setProductId(1L);
        productSnapshot.setName("product");
        productSnapshot.setProductCategory(ProductCategory.CEREAL);
        productSnapshot.setCalories(400);
        productSnapshot.setProtein(40);
        productSnapshot.setCarbs(4);
        productSnapshot.setFat(1);
        productQuantity.setProductQuantityId(1L);
        productQuantity.setQuantity(2.0);
    }

    @Test
    @DisplayName("should map ProductUpdatedEvent to ProductSnapshot correctly")
    public void shouldMapProductUpdatedEventToProductSnapshotCorrectly() {
        ProductSnapshot mappedSnapshot = ProductSnapshotMapper.mapProductUpdatedEventToSnapshotUpdate(productUpdatedEvent, productSnapshot);

        assertEquals("updated", mappedSnapshot.getName());
        assertEquals(ProductCategory.FISH, mappedSnapshot.getProductCategory());
        assertEquals(100, mappedSnapshot.getCalories());
        assertEquals(10, mappedSnapshot.getProtein());
        assertEquals(1, mappedSnapshot.getCarbs());
        assertEquals(0, mappedSnapshot.getFat());
    }

    @Test
    @DisplayName("should map ProductSnapshot to ProductDtoRead correctly")
    public void shouldMapProductSnapshotToProductDtoReadCorrectly() {
        ProductDtoRead mapped = ProductSnapshotMapper.mapProductSnapshotToProductDtoRead(productSnapshot, productQuantity);

        assertEquals(1L, mapped.productId());
        assertEquals("product", mapped.name());
        assertEquals(ProductCategory.CEREAL, mapped.productCategory());
        assertEquals(400, mapped.calories());
        assertEquals(40, mapped.protein());
        assertEquals(4, mapped.carbs());
        assertEquals(1, mapped.fat());
        assertEquals(2.0, mapped.quantity());
    }
}
