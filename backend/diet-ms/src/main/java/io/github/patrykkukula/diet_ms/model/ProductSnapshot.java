package io.github.patrykkukula.diet_ms.model;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ProductSnapshot {
    @Id
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private ProductCategory productCategory;

    @Column(nullable = false)
    private Integer calories;

    @Column(nullable = false)
    private Integer protein;

    @Column(nullable = false)
    private Integer carbs;

    @Column(nullable = false)
    private Integer fat;

    private String ownerUsername;

}
