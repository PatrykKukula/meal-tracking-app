package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductCreatedEvent(String eventId,
                                  Long productId,
                                  String name,
                                  String productCategory,
                                  Integer calories,
                                  Integer protein,
                                  Integer carbs,
                                  Integer fat,
                                  String ownerUsername)
implements BasicProductEvent {
    @Override
    public String getEventId() {
        return this.eventId;
    }

    @Override
    public String routingKey() {
        return "product.added";
    }
}
