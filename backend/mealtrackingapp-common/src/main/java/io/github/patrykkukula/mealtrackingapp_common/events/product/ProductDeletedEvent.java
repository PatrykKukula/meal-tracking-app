package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductDeletedEvent(String eventId, Long productId)
implements BasicProductEvent {
    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public String routingKey() {
        return "product.deleted";
    }
}
