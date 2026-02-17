package io.github.patrykkukula.events;

public record ProductUpdatedEvent(Long productId,
                                  String name,
                                  String productCategory,
                                  Integer calories,
                                  Integer protein,
                                  Integer carbs,
                                  Integer fat,
                                  String ownerUsername) {
}
