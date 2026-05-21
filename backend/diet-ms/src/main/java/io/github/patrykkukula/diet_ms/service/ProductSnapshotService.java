package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.exception.ProductSnapshotNotFoundException;
import io.github.patrykkukula.diet_ms.mapper.ProductSnapshotMapper;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductCreatedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSnapshotService {
    private final ProductSnapshotRepository productSnapshotRepository;

    public void addProductSnapshot(ProductCreatedEvent event) {
        ProductSnapshot productSnapshot = productSnapshotRepository.save(ProductSnapshot.fromEvent(event));

        log.info("ProductSnapshot created: {}", productSnapshot);
    }

    public void updateProductSnapshot(ProductUpdatedEvent event) {
        ProductSnapshot snapshot = findById(event.productId());

        ProductSnapshot updatedSnapshot = ProductSnapshotMapper.mapProductUpdatedEventToSnapshotUpdate(event, snapshot);

        log.info("ProductSnapshot updated: {}", updatedSnapshot);
    }

    public void deleteProductSnapshot(Long productId) {
        productSnapshotRepository.deleteById(productId);

        log.info("ProductSnapshot deleted with ID: {}", productId);
    }

    private ProductSnapshot findById(Long id) {
        return productSnapshotRepository.findById(id).orElseThrow(() -> new ProductSnapshotNotFoundException(id));
    }
}
