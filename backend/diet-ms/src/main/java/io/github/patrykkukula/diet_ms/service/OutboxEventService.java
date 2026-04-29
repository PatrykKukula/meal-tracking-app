package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.factory.ProductEventFactory;
import io.github.patrykkukula.diet_ms.repository.OutboxEventRepository;
import io.github.patrykkukula.mealtrackingapp_common.events.EventBindingConfig;
import io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus;
import io.github.patrykkukula.mealtrackingapp_common.events.ProductEventSender;
import io.github.patrykkukula.mealtrackingapp_common.events.product.BasicProductEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventService {
    private final OutboxEventRepository repository;
    private final ProductEventSender eventSender;
    private final ProductEventFactory eventFactory;

    /*
        Method for sending events in fixed rate in Outbox pattern
     */
    @Scheduled(fixedRate = 30000)
    public void sendEvents() {
        log.info("Invoking sendEvents() in product_ms");

        AtomicInteger count = new AtomicInteger();

        repository.getUnsentEvents(PageRequest.of(0, 100, Sort.by("createdAt").ascending()))
                .forEach(event -> {
                    BasicProductEvent productEvent = eventFactory.createEvent(event);
                    log.info("Attempt to send event. Event type: {}, payload: {}", event.getEventType(),
                            event.getPayload());

                    try {
                        eventSender.sendEvent(productEvent);
                        event.setStatus(OutboxEventStatus.SENT);
                        event.setSentAt(LocalDateTime.now());
                    } catch (Exception ex) {
                        log.warn("Failed to send event: {}", event.getOutboxEventId(), ex);
                        if (event.getRetryCount() >= 5) {
                            log.warn("Retry count limit exceed. Mark event DEAD");
                            event.setStatus(OutboxEventStatus.DEAD);                // mark dead if event failed to send too many times
                        }
                        else {
                            event.setStatus(OutboxEventStatus.FAILED);
                            event.setRetryCount(event.getRetryCount()+1);
                        }
                    } finally {
                        repository.save(event);
                    }
                });
        log.info("Events send: {}", count.intValue());
    }

    /*
        Method to remove sent or dead events
     */
    @Transactional
    @Scheduled(cron = "0 * * * * MON-FRI")
    public void removeEvents() {
        log.info("Invoking removeEvents() in product_ms");

        int removed = repository.deleteSentEvents();

        log.info("Removed events: {}", removed);
    }
}
