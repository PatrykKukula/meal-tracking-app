package io.github.patrykkukula.mealtrackingapp_common.events;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@ConfigurationProperties(prefix = "app.bindings")
@Component
public record EventBindingConfig(Map<String, String> bindings) {

    /**
     *
     * @param routingKey - Event routingKey
     * @return binging name for given routingKey
     */
    public String getBinding(String routingKey) {
        String binding = bindings.get(routingKey);

        if (binding == null) {
            throw new IllegalArgumentException("Event binding not found for routingKey: %s".formatted(routingKey));
        }
        return binding;
    }
}
