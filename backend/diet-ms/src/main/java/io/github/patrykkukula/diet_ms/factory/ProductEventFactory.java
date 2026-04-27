package io.github.patrykkukula.diet_ms.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.diet_ms.model.OutboxEvent;
import io.github.patrykkukula.mealtrackingapp_common.events.EventType;
import io.github.patrykkukula.mealtrackingapp_common.events.product.BasicProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventFactory {
    private final ObjectMapper mapper;

    public BasicProductEvent createEvent(OutboxEvent event) {
                return fromOutboxEvent(event);
        }

    private BasicProductEvent fromOutboxEvent(OutboxEvent event) {
        Class<? extends BasicProductEvent> eventClass = EventType.getClassFromRoutingKey(event.getEventType().getRoutingKey());

        try {
            return mapper.readValue(event.getPayload(), eventClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error during ProductEvent mapping", e);
        }
    }
}
