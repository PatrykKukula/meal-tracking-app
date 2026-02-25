package io.github.patrykkukula.diet_ms.builder;

import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;

import java.util.ArrayList;
import java.util.List;

public class MealTestBuilder {
    private Long id = 1L;
    private String name = "meal";
    private List<ProductQuantity> quantities = new ArrayList();
    private DietDay dietDay;

    private MealTestBuilder() {}

    public static MealTestBuilder meal() {
        return new MealTestBuilder();
    }

    public MealTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MealTestBuilder quantities(List<ProductQuantity> quantities) {
        this.quantities = quantities;
        return this;
    }

    public MealTestBuilder dietDay(DietDay dietDay) {
        this.dietDay = dietDay;
        return this;
    }

    public Meal build() {
        Meal meal = new Meal();
        meal.setMealId(id);
        meal.setName(name);
        meal.setProductQuantities(quantities);
        meal.setDietDay(dietDay);
        return meal;
    }
}
