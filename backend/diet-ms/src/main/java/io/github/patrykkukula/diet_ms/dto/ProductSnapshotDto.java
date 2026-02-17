package io.github.patrykkukula.diet_ms.dto;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;

public record ProductSnapshotDto(Long productId,
                                 String name,
                                 ProductCategory productCategory,
                                 Integer calories,
                                 Integer protein,
                                 Integer carbs,
                                 Integer fat,
                                 String ownerUsername
                        ) {}