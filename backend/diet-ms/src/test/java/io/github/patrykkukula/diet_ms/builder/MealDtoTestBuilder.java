package io.github.patrykkukula.diet_ms.builder;

import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;

import java.util.ArrayList;
import java.util.List;

public class MealDtoTestBuilder {
    private String name = "meal";
    private List<ProductQuantityDto> quantities = new ArrayList<>();
    private MealDtoTestBuilder() {}

    public static MealDtoTestBuilder meal() {
        return new MealDtoTestBuilder();
    }

    public MealDtoTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MealDtoTestBuilder quantities(List<ProductQuantityDto> quantities) {
        this.quantities = quantities;
        return this;
    }

    public MealDto build() {
        MealDto mealDto = new MealDto();
        mealDto.setName(name);
        mealDto.setQuantities(quantities);
        return mealDto;
    }
}
