package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.builder.ProductSnapshotTestBuilder;
import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductUpdatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ProductSnapshotService.class)
public class ProductSnapshotServiceIntegrationTest {
    @Autowired
    private ProductSnapshotRepository repository;
    @Autowired
    private ProductSnapshotService service;

    @Test
    @DisplayName("should update ProductSnapshot correctly")
    public void shouldUpdateProductSnapshotCorrectly() {
        ProductSnapshot snapshot = ProductSnapshotTestBuilder.productSnapshot()
                        .name("product")
                        .build();

        repository.save(snapshot);

        ProductUpdatedEvent event = new ProductUpdatedEvent(
                snapshot.getProductId(),
                "updated product",
                ProductCategory.MEAT.name(),
                200,
                10,
                20,
                5,
                "user"
        );

        service.updateProductSnapshot(event);

        repository.flush();

        Optional<ProductSnapshot> updatedSnapshot = repository.findById(event.productId());

        assertTrue(updatedSnapshot.isPresent());
        assertEquals("updated product", updatedSnapshot.get().getName());
    }
}
