package io.github.patrykkukula.statistics_ms.function;

import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductAddedToMealEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductRemovedFromMealEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductUpdatedInMealEvent;
import io.github.patrykkukula.statistics_ms.dto.ProductCountDto;
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
            log.info("ProductAddedToMeal Event received in statistics_ms for product ID: {}", event.productId());

            ProductCountDto productCountDto = statisticsService.addProductToProductCount(event);


        };
    }

    public Consumer<ProductRemovedFromMealEvent> productRemovedFromMealEvent() {
        return event -> {
            log.info("ProductRemovedFromMeal Event received in statistics_ms for product ID: {}", event.productId());

            ProductCountDto productCountDto = statisticsService.removeProductFromProductCount(event);

            productCountUpdatedSuccessfullyLog(productCountDto);
        };
    }

    public Consumer<ProductUpdatedInMealEvent> productUpdatedInMealEvent() {
        return event -> {
            log.info("ProductUpdatedInMeal Event received in statistics_ms for product ID: {}", event.productId());

            ProductCountDto productCountDto = statisticsService.updateProductInProductCount(event);

            productCountUpdatedSuccessfullyLog(productCountDto);
        };
    }

    private void productCountUpdatedSuccessfullyLog(ProductCountDto productCountDto) {
        log.info("ProductCount updated successfully: {}", productCountDto);
    }
}
