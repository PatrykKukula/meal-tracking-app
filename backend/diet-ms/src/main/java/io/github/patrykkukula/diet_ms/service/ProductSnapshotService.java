package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.dto.ProductDtoRead;
import io.github.patrykkukula.diet_ms.exception.ProductSnapshotNotFoundException;
import io.github.patrykkukula.diet_ms.mapper.ProductSnapshotMapper;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.events.ProductCreatedEvent;
import io.github.patrykkukula.events.ProductUpdatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSnapshotService {
    private final ProductSnapshotRepository productSnapshotRepository;
    
    public List<ProductDtoRead> getProductsForMeal(Meal meal) {
        return meal.getProductQuantities()
                .stream()
                .map(quantity -> {
                    return ProductSnapshotMapper.mapProductSnapshotToProductRead(quantity.getProductSnapshot(), quantity);
                }).toList();
    }

    public void addProductSnapshot(ProductCreatedEvent event) {
        ProductSnapshot productSnapshot = productSnapshotRepository.save(ProductSnapshot.fromEvent(event));
        log.info("ProductSnapshot created: {}", productSnapshot);
    }

    @Transactional
    public void updateProductSnapshot(ProductUpdatedEvent event) {
        ProductSnapshot snapshot = findById(event.productId());

        ProductSnapshot updatedSnapshot = ProductSnapshotMapper.mapProductDtoToSnapshotUpdate(event, snapshot);

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
