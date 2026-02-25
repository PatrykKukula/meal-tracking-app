package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.builder.ProductQuantityTestBuilder;
import io.github.patrykkukula.diet_ms.builder.ProductSnapshotTestBuilder;
import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.dto.ProductDtoRead;
import io.github.patrykkukula.diet_ms.exception.ProductSnapshotNotFoundException;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.mealtrackingapp_common.events.ProductCreatedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.ProductUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductSnapshotServiceUnitTest {
    @Mock
    private ProductSnapshotRepository productSnapshotRepository;
    @InjectMocks
    private ProductSnapshotService productSnapshotService;

    private Meal meal = new Meal();
    private ProductQuantity productQuantity;
    private ProductSnapshot productSnapshot;
    private ProductCreatedEvent productCreatedEvent;
    private ProductUpdatedEvent productUpdatedEvent;

    @BeforeEach
    public void setUp() {
        productSnapshot = ProductSnapshotTestBuilder.productSnapshot()
                        .name("snapshot")
                        .productCategory(ProductCategory.MEAT)
                        .calories(200)
                        .protein(200)
                        .carbs(200)
                        .fat(200)
                        .build();
        productQuantity = ProductQuantityTestBuilder.productQuantity()
                        .quantity(2.0)
                        .snapshot(productSnapshot)
                        .build();

        meal.setProductQuantities(List.of(productQuantity));

        productCreatedEvent = new ProductCreatedEvent(
                1L, "product", ProductCategory.FISH.name(), 100,
                100, 100, 100, "user"
        );

        productUpdatedEvent = new ProductUpdatedEvent(
                1L, "updated product", ProductCategory.CEREAL.name(), 100,
                100, 100, 100, "user"
        );
    }

    @Test
    @DisplayName("should get Products for Meal correctly")
    public void shouldGetProductsForMealCorrectly() {
        List<ProductDtoRead> productsForMeal = productSnapshotService.getProductsForMeal(meal);

        assertEquals(1, productsForMeal.size());
        assertEquals(2.0, productsForMeal.getFirst().quantity());
    }

    @Test
    @DisplayName("should add ProductSnapshot correctly")
    public void shouldAddProductSnapshotCorrectly() {
        ArgumentCaptor<ProductSnapshot> captor = ArgumentCaptor.forClass(ProductSnapshot.class);
        productSnapshotService.addProductSnapshot(productCreatedEvent);

        verify(productSnapshotRepository).save(captor.capture());

        ProductSnapshot value = captor.getValue();

        assertEquals("product", value.getName());
        assertEquals(ProductCategory.FISH, value.getProductCategory());
    }

    @Test
    @DisplayName("should delete ProductSnapshot correctly")
    public void shouldDeleteProductSnapshotCorrectly() {
        doNothing().when(productSnapshotRepository).deleteById(anyLong());

        productSnapshotService.deleteProductSnapshot(1L);

        verify(productSnapshotRepository, times(1)).deleteById(eq(1L));
    }

    @Test
    @DisplayName("should throw ProductSnapshotNotFoundExceptionWhenUpdateProductSnapshotAndProductNotFound")
    public void shouldThrowProductSnapshotNotFoundExceptionWhenUpdateProductSnapshotAndProductNotFound() {
        when(productSnapshotRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductSnapshotNotFoundException.class, () ->productSnapshotService.updateProductSnapshot(productUpdatedEvent));
    }
}
