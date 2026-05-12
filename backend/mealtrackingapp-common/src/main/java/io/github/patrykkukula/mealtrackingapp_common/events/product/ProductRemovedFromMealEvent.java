package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductRemovedFromMealEvent (
        String eventId,
        Long productId,
        String username,
        Double quantity
) implements BasicProductEvent {
    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public String routingKey() {
        return "product.removed.from.meal";
    }
}
