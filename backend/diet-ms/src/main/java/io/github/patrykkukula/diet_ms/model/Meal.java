package io.github.patrykkukula.diet_ms.model;

import io.github.patrykkukula.diet_ms.dto.MealDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mealId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "diet_day_id")
    private DietDay dietDay;

    @Column(nullable = false)
    private Long orderIndex;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "meal")
    private List<ProductQuantity> productQuantities = new ArrayList<>();

    public void addProductQuantity(ProductQuantity productQuantity) {
        productQuantities.add(productQuantity);
        productQuantity.setMeal(this);
    }

    public static Meal fromDto(MealDto dto) {
        Meal meal = new Meal();
        meal.setName(dto.getName());
        return meal;
    }
}
