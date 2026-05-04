package io.github.patrykkukula.statistics_ms.function;

import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductAddedToMealEvent;
import io.github.patrykkukula.statistics_ms.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProductFunctions {
    private final StatisticsService statisticsService;

    public Consumer<ProductAddedToMealEvent> productAddedToMealEvent() {
        return event -> {
            statisticsService.addProductToProductCount(event);
            log.info("ProductAddedToMeal Event received in statistics_ms for product ID: {}", event.productId());
        };
    }
}
