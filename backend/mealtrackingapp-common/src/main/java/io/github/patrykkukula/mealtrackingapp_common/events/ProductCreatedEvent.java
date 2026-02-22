package io.github.patrykkukula.mealtrackingapp_common.events;

public record ProductCreatedEvent(Long productId,
                                  String name,
                                  String productCategory,
                                  Integer calories,
                                  Integer protein,
                                  Integer carbs,
                                  Integer fat,
                                  String ownerUsername) {
}
