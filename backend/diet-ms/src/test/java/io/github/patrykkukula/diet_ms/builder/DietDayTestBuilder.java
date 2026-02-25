package io.github.patrykkukula.diet_ms.builder;

import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;

import java.util.HashSet;
import java.util.Set;

public class DietDayTestBuilder {
    private Long id = 1L;
    private String owner = "user";
    private Set<Meal> meals = new HashSet<>();

    private DietDayTestBuilder() {}

    public static DietDayTestBuilder dietDay() {
        return new DietDayTestBuilder();
    }

    public DietDayTestBuilder owner(String owner) {
        this.owner = owner;
        return this;
    }

    public DietDayTestBuilder meals(Set<Meal> meals) {
        this.meals = meals;
        return this;
    }

    public DietDay build() {
        DietDay dietDay = new DietDay();
        dietDay.setDietDayId(id);
        dietDay.setOwnerUsername(owner);
        dietDay.setMeals(meals);
        return dietDay;
    }
}
