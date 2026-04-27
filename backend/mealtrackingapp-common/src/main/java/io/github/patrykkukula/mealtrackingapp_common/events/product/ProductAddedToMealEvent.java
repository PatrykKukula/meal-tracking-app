package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductAddedToMealEvent(
        String productName,
        Long productId,
        Double quantity,
        String username
) implements BasicProductEvent {

    @Override
    public String routingKey() {
        return "product.added.to.meal";
    }
}
