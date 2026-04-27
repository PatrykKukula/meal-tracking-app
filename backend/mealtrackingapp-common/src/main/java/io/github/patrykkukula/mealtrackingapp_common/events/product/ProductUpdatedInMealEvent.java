package io.github.patrykkukula.mealtrackingapp_common.events.product;

public record ProductUpdatedInMealEvent(
        Long productId,
        String username,
        Double oldQuantity,
        Double newQuantity
) implements BasicProductEvent {
    @Override
    public String routingKey() {
        return "product.updated.in.meal";
    }
}
