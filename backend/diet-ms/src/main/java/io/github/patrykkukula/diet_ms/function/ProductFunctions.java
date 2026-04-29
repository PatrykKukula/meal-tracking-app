package io.github.patrykkukula.diet_ms.function;

import io.github.patrykkukula.diet_ms.service.ProductSnapshotService;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductCreatedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductDeletedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProductFunctions {
    private final ProductSnapshotService productSnapshotService;

    @Bean
    public Consumer<ProductCreatedEvent> productCreated() {
        return event -> {
          log.info("ProductCreated Event received in diet_ms for product ID: {}", event.productId());
          productSnapshotService.addProductSnapshot(event);
        };
    }

    @Bean
    public Consumer<ProductUpdatedEvent> productUpdated() {
        return event -> {
            log.info("ProductUpdated Event received in diet_ms for product ID: {}", event.productId());
            productSnapshotService.updateProductSnapshot(event);
        };
    }

    @Bean
    public Consumer<ProductDeletedEvent> productDeleted() {
        return event -> {
            log.info("ProductDeleted Event received in diet_ms for product ID: {}", event.productId());
            productSnapshotService.deleteProductSnapshot(event.productId());
        };
    }
}
