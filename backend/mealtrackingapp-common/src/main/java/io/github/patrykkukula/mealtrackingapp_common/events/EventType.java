package io.github.patrykkukula.mealtrackingapp_common.events;

import io.github.patrykkukula.mealtrackingapp_common.events.product.*;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum EventType {
    PRODUCT_ADDED_TO_MEAL("product.added.to.meal", ProductAddedToMealEvent.class),
    PRODUCT_REMOVED_FROM_MEAL("product.removed.from.meal", ProductRemovedFromMealEvent.class),
    PRODUCT_UPDATED_IN_MEAL("product.updated.in.meal", ProductUpdatedInMealEvent.class),
    PRODUCT_ADDED("product.added", ProductCreatedEvent.class),
    PRODUCT_UPDATED("product.updated", ProductUpdatedEvent.class),
    PRODUCT_DELETED("product.deleted", ProductDeletedEvent.class);

    private final String routingKey;
    private final Class<? extends BasicProductEvent> eventClass;

    /**
     *
     * @param routingKey of the Event
     * @return EventType based on Event routingKey or throws exception if none found
     */
    public static EventType fromRoutingKey(String routingKey) {
        return Arrays.stream(values())
                .filter(val -> val.routingKey.equals(routingKey))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid routing key"));
    }

    /**
     *
     * @param routingKey of the Event
     * @return class of the Event based on Event routingKey or throws exception if none found
     */
    public static Class<? extends BasicProductEvent> getClassFromRoutingKey(String routingKey) {
        return Arrays.stream(values())
                .filter(val -> val.routingKey.equals(routingKey))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid routing key"))
                .getEventClass();
    }
}

