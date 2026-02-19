package io.github.patrykkukula.diet_ms.assembler;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.exception.ProductSnapshotNotFoundException;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.diet_ms.security.AuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Assembler class to assemble DietDay entity and set bidirectional relation mapping for Meal and ProductQuantity entities
 */
@Component
@RequiredArgsConstructor
public class DietDayAssembler {
    private final ProductSnapshotRepository productSnapshotRepository;
    private final AuthenticationUtils authenticationUtils;
    private final String DEFAULT_MEAL_NAME = "Meal";

    public DietDay assemble(DietDayDto dietDayDto) {
        DietDay dietDay = DietDay.fromDto(dietDayDto);

        dietDay.setOwnerUsername(authenticationUtils.getAuthenticatedUserUsername());

        dietDayDto.getMeals()
                .forEach(mealDto -> addMealToDietDay(mealDto, dietDay));

        return dietDay;
    }

    private void addMealToDietDay(MealDto mealDto, DietDay dietDay) {
        if (mealDto.getName() == null || mealDto.getName().isEmpty()) {
            mealDto.setName(DEFAULT_MEAL_NAME);
        }

        Meal meal = Meal.fromDto(mealDto);

        mealDto.getQuantities()
                .forEach(qty -> addProductQuantityToMeal(qty, meal));

        dietDay.addMeal(meal);
    }

    private void addProductQuantityToMeal(ProductQuantityDto productQuantityDto, Meal meal) {
        ProductQuantity productQuantity = ProductQuantity.fromDto(productQuantityDto);

        ProductSnapshot productSnapshot = productSnapshotRepository.findById(productQuantityDto.getProductId())
                .orElseThrow(() -> new ProductSnapshotNotFoundException(productQuantityDto.getProductId()));

        productQuantity.setProductSnapshot(productSnapshot);
        productSnapshot.getProductQuantities().add(productQuantity);

        meal.addProductQuantity(productQuantity);
    }
}
