package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.dto.ProductDto;
import io.github.patrykkukula.diet_ms.mapper.ProductSnapshotMapper;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSnapshotService {
    private final ProductSnapshotRepository productSnapshotRepository;

    public void addProductSnapshot(ProductDto productDto) {
        ProductSnapshot productSnapshot = productSnapshotRepository.save(ProductSnapshotMapper.mapProductDtoToSnapshot(productDto));
        log.info("ProductSnapshot created: {}", productSnapshot);
    }

    @Transactional
    public void updateProductSnapshot(ProductDto productDto) {
        productSnapshotRepository.findById(productDto.productId()).ifPresent(
                    snapshot -> {
                        ProductSnapshotMapper.mapProductDtoToSnapshotUpdate(productDto, snapshot);
                        log.info("ProductSnapshot updated: {}", snapshot);
                    });
    }

    public void deleteProductSnapshot(Long productId) {
        productSnapshotRepository.deleteById(productId);
        log.info("ProductSnapshot deleted with ID: {}", productId);
    }

}
