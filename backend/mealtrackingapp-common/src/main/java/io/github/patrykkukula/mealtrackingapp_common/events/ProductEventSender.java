package io.github.patrykkukula.mealtrackingapp_common.events;

import io.github.patrykkukula.mealtrackingapp_common.events.product.BasicProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventSender {
    private final StreamBridge streamBridge;
    private final EventBindingConfig  eventBindingConfig;

    public void sendEvent(BasicProductEvent event) {
        String binding = eventBindingConfig.getBinding(event.routingKey());

        boolean send = streamBridge.send(binding, event);

        if (!send) {
            throw new RuntimeException("Failed to send event");
        }

        log.info("event sent for binding: {}", binding);
    }
}
