package io.github.patrykkukula.diet_ms.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.diet_ms.model.OutboxEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.EventType;
import io.github.patrykkukula.mealtrackingapp_common.events.OutboxEventStatus;
import io.github.patrykkukula.mealtrackingapp_common.events.product.BasicProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventFactory {
    private final ObjectMapper objectMapper;

    public OutboxEvent create(BasicProductEvent event) {
        log.info("Creating OutboxEvent for routingKey: {}", event.routingKey());
        try {
            return new OutboxEvent(
                    null,
                    OutboxEventStatus.NEW,
                    EventType.fromRoutingKey(event.routingKey()),
                    objectMapper.writeValueAsString(event),
                    LocalDateTime.now(),
                    null,
                    0
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error processing Event to Json", e);
        }
    }
}
