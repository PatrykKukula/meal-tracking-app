package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MealMapperUnitTest {
    private Meal meal = new Meal();
    private ProductQuantity productQuantity = new ProductQuantity();

    @BeforeEach
    public void setUp() {
        productQuantity.setQuantity(2.0);
        meal.setName("breakfast");
        meal.setProductQuantities(List.of(productQuantity));
    }

    @Test
    @DisplayName("should map Meal to MealDto correctly")
    public void shouldMapMealToMealDtoCorrectly() {
        MealDto mappedMeal = MealMapper.mapMealToMealDto(meal);

        assertEquals("breakfast", mappedMeal.getName());
        assertEquals(1, meal.getProductQuantities().size());
    }
}
