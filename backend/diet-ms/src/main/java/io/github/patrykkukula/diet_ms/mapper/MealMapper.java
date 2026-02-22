package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.model.Meal;

public class MealMapper {
    private MealMapper() {}

    public static MealDto mapMealToMealDto(Meal meal) {
        MealDto mealDto = new MealDto();
        mealDto.setName(meal.getName());
        mealDto.setQuantities(meal.getProductQuantities()
                .stream()
                .map(ProductQuantityMapper::mapProductQuantityToProductQuantityDto)
                .toList());
        return mealDto;
    }
}
