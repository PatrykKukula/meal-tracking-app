package io.github.patrykkukula.diet_ms.function;

import io.github.patrykkukula.diet_ms.model.ProcessedEvent;
import io.github.patrykkukula.diet_ms.repository.ProcessedEventRepository;
import io.github.patrykkukula.diet_ms.service.ProductSnapshotService;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductCreatedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductDeletedEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductUpdatedEvent;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
/*
    Class to maintain transaction between saving processed events and services
 */
public class ProductEventHandler {
    private final ProductSnapshotService productSnapshotService;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        log.info("ProductCreated Event received in diet_ms for product ID: {}", event.productId());

        try {
            processedEventRepository.save(new ProcessedEvent(event.eventId()));
        }
        catch (DataIntegrityViolationException ex) {
            logAlreadyProcessed(event.eventId());
            return;
        }

        productSnapshotService.addProductSnapshot(event);
    };

    @Transactional
    public void handleProductUpdatedEvent(ProductUpdatedEvent event) {
        log.info("ProductUpdated Event received in diet_ms for product ID: {}", event.productId());

        try {
            processedEventRepository.save(new ProcessedEvent(event.eventId()));
        }
        catch (DataIntegrityViolationException ex) {
            logAlreadyProcessed(event.eventId());
            return;
        }

        productSnapshotService.updateProductSnapshot(event);
    }

    @Transactional
    public void handleProductDeletedEvent(ProductDeletedEvent event) {
        log.info("ProductDeleted Event received in diet_ms for product ID: {}", event.productId());

        try {
            processedEventRepository.save(new ProcessedEvent(event.eventId()));
        }
        catch (DataIntegrityViolationException ex) {
            logAlreadyProcessed(event.eventId());
            return;
        }

        productSnapshotService.deleteProductSnapshot(event.productId());
    }

    private void logAlreadyProcessed(String eventId) {
        log.info("Event with ID: {} already processed", eventId);
    }
}
