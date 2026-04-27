package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.constants.OutboxEventStatus;
import io.github.patrykkukula.diet_ms.factory.ProductEventFactory;
import io.github.patrykkukula.diet_ms.repository.OutboxEventRepository;
import io.github.patrykkukula.mealtrackingapp_common.events.EventBindingConfig;
import io.github.patrykkukula.mealtrackingapp_common.events.ProductEventSender;
import io.github.patrykkukula.mealtrackingapp_common.events.product.BasicProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventService {
    private final OutboxEventRepository repository;
    private final ProductEventSender eventSender;
    private final ProductEventFactory eventFactory;
    private final EventBindingConfig eventBindingConfig;

    /*
        Method for sending events in fixed rate in Outbox pattern
     */
    @Scheduled(fixedRate = 1000)
    public void sendEvents() {
        repository.getUnsentEvents(PageRequest.of(0, 100, Sort.by("createdAt").ascending()))
                .forEach(event -> {
                    BasicProductEvent productEvent = eventFactory.createEvent(event);

                    try {
                        eventSender.sendEvent(productEvent);
                        event.setStatus(OutboxEventStatus.SENT);
                        event.setSentAt(LocalDateTime.now());
                    }
                    catch (Exception ex) {
                        log.warn("Failed to send event: {}", event.getOutboxEventId(), ex);
                        if (event.getRetryCount() >= 5) {
                            event.setStatus(OutboxEventStatus.DEAD);                // mark dead if event failed to send too many times
                        }
                        else {
                            event.setStatus(OutboxEventStatus.FAILED);
                            event.setRetryCount(event.getRetryCount()+1);
                        }
                    }
                });
    }

    /*
        Method to remove sent or dead events
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void removeEvents() {
        repository.deleteSentEvents();
    }
}
