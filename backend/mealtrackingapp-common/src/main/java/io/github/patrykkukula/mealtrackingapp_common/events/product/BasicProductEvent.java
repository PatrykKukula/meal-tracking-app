package io.github.patrykkukula.mealtrackingapp_common.events.product;

public interface BasicProductEvent {
    String getEventId();
    String routingKey();
}
