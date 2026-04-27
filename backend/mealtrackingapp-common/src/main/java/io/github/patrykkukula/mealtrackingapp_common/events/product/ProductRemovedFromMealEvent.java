package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductRemovedFromMealEvent (
        Long productId,
        String username,
        Double quantity
) implements BasicProductEvent {
    @Override
    public String routingKey() {
        return "product.removed.from.meal";
    }
}
