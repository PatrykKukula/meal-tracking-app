package io.github.patrykkukula.diet_ms.model;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.mealtrackingapp_common.events.ProductCreatedEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@ToString
public class ProductSnapshot {
    @Id
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Enumerated(value = EnumType.STRING)
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productSnapshot")
    private List<ProductQuantity> productQuantities = new ArrayList<>();

    public static ProductSnapshot fromEvent(ProductCreatedEvent event) {
        ProductSnapshot productSnapshot = new ProductSnapshot();
        productSnapshot.setProductId(event.productId());
        productSnapshot.setName(event.name());
        productSnapshot.setProductCategory(ProductCategory.valueOf(event.productCategory()));
        productSnapshot.setCalories(event.calories());
        productSnapshot.setProtein(event.protein());
        productSnapshot.setCarbs(event.carbs());
        productSnapshot.setFat(event.fat());
        productSnapshot.setOwnerUsername(event.ownerUsername() != null ? event.ownerUsername() : null);
        return productSnapshot;
    }
}
