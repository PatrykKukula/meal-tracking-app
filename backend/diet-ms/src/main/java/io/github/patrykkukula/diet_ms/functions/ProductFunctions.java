package io.github.patrykkukula.diet_ms.functions;

import io.github.patrykkukula.diet_ms.dto.ProductSnapshotDto;
import io.github.patrykkukula.diet_ms.service.ProductSnapshotService;
import io.github.patrykkukula.events.ProductCreatedEvent;
import io.github.patrykkukula.events.ProductUpdatedEvent;
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
          log.info("ProductCreated Event received in diet_ms");
          productSnapshotService.addProductSnapshot(event);
        };
    }

    @Bean
    public Consumer<ProductUpdatedEvent> productUpdated() {
        return event -> {
            log.info("ProductUpdated Event received in diet_ms");
            productSnapshotService.updateProductSnapshot(event);
        };
    }

    @Bean
    public Consumer<Long> productDeleted() {
        return productId -> {
            log.info("ProductDeleted Event received in diet_ms");
            productSnapshotService.deleteProductSnapshot(productId);
        };
    }
}
