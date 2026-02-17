package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.dto.ProductSnapshotDto;
import io.github.patrykkukula.diet_ms.mapper.ProductSnapshotMapper;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.events.ProductCreatedEvent;
import io.github.patrykkukula.events.ProductUpdatedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSnapshotService {
    private final ProductSnapshotRepository productSnapshotRepository;

    public void addProductSnapshot(ProductCreatedEvent event) {
        ProductSnapshot productSnapshot = productSnapshotRepository.save(ProductSnapshotMapper.mapProductCreatedEventToSnapshot(event));
        log.info("ProductSnapshot created: {}", productSnapshot);
    }

    @Transactional
    public void updateProductSnapshot(ProductUpdatedEvent event) {
        productSnapshotRepository.findById(event.productId()).ifPresent(
                    snapshot -> {
                        ProductSnapshotMapper.mapProductDtoToSnapshotUpdate(event, snapshot);
                        log.info("ProductSnapshot updated: {}", snapshot);
                    });
    }

    public void deleteProductSnapshot(Long productId) {
        productSnapshotRepository.deleteById(productId);
        log.info("ProductSnapshot deleted with ID: {}", productId);
    }

}
