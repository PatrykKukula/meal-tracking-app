package io.github.patrykkukula.diet_ms.model;

import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class ProductQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productQuantityId;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private ProductSnapshot productSnapshot;

    @Column(nullable = false)
    private Double quantity;

    @ManyToOne
    @JoinColumn(name = "mealId", nullable = false)
    private Meal meal;

    public static ProductQuantity fromDto(ProductQuantityDto dto) {
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setQuantity(dto.getQuantity());
        return productQuantity;
    }
}
