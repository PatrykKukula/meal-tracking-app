package io.github.patrykkukula.diet_ms.functions;

import io.github.patrykkukula.diet_ms.dto.ProductDto;
import io.github.patrykkukula.diet_ms.service.ProductSnapshotService;
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
    public Consumer<ProductDto> productCreated() {
        return productDto -> {
          log.info("ProductCreated Event received in diet_ms");
          productSnapshotService.addProductSnapshot(productDto);
        };
    }

    @Bean
    public Consumer<ProductDto> productUpdated() {
        return productDto -> {
            log.info("ProductUpdated Event received in diet_ms");
            productSnapshotService.updateProductSnapshot(productDto);
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
