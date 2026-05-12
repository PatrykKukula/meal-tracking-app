package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductAddedToMealEvent(
        String eventId,
        String productName,
        Long productId,
        Double quantity,
        String username
) implements BasicProductEvent{

    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public String routingKey() {
        return "product.added.to.meal";
    }
}
