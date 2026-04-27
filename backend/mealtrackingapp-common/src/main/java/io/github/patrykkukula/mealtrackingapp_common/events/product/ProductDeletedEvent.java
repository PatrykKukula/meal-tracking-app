package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductDeletedEvent(Long productId)
implements BasicProductEvent {
    @Override
    public String routingKey() {
        return "product.deleted";
    }
}
