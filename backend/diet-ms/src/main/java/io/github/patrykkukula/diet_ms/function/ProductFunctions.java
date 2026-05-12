package io.github.patrykkukula.diet_ms.function;

import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductCreatedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductDeletedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class ProductFunctions {
    private final ProductEventHandler productEventHandler;

    @Bean
    public Consumer<ProductCreatedEvent> productCreated() {
        return productEventHandler::handleProductCreatedEvent;
    }

    @Bean
    public Consumer<ProductUpdatedEvent> productUpdated() {
        return productEventHandler::handleProductUpdatedEvent;
    }

    @Bean
    public Consumer<ProductDeletedEvent> productDeleted() {
        return productEventHandler::handleProductDeletedEvent;
    }
}
