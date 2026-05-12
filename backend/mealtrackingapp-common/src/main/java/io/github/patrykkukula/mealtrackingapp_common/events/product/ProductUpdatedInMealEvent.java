package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductUpdatedInMealEvent(
        String eventId,
        Long productId,
        String username,
        Double oldQuantity,
        Double newQuantity
) implements BasicProductEvent {
    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public String routingKey() {
        return "product.updated.in.meal";
    }
}
