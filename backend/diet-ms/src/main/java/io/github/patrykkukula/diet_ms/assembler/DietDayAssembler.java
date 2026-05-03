package io.github.patrykkukula.diet_ms.assembler;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.exception.ProductSnapshotNotFoundException;
import io.github.patrykkukula.diet_ms.factory.OutboxEventFactory;
import io.github.patrykkukula.diet_ms.mapper.MealMapper;
import io.github.patrykkukula.diet_ms.mapper.ProductQuantityMapper;
import io.github.patrykkukula.diet_ms.model.*;
import io.github.patrykkukula.diet_ms.repository.OutboxEventRepository;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.diet_ms.security.AuthenticationUtils;
import io.github.patrykkukula.mealtrackingapp_common.events.product.ProductAddedToMealEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Assembler class to assemble DietDay entity and set bidirectional relation mapping for Meal and ProductQuantity entities
 */
@Component
@RequiredArgsConstructor
public class DietDayAssembler {
    private final ProductSnapshotRepository productSnapshotRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final AuthenticationUtils authenticationUtils;
    private final OutboxEventFactory eventFactory;
    private final String DEFAULT_MEAL_NAME = "Meal";

    public DietDay assemble(DietDayDto dietDayDto) {
        DietDay dietDay = DietDay.fromDto(dietDayDto);

        String username = authenticationUtils.getAuthenticatedUserUsername();
        dietDay.setOwnerUsername(username);

        dietDayDto.getMeals()
                .forEach(mealDto -> {
                    addMealToDietDay(mealDto, dietDay, username);                      // saving outbox event handled here
                });

        return dietDay;
    }

    public MealDto addMealToDietDay(MealDto mealDto, DietDay dietDay, String username) {
        if (mealDto.getName() == null || mealDto.getName().isEmpty()) {
            mealDto.setName(DEFAULT_MEAL_NAME);
        }

        Meal meal = Meal.fromDto(mealDto);

        meal.setOrderIndex((long) dietDay.getMeals().size());

        mealDto.getQuantities()
                .forEach(qty -> addProductQuantityToMeal(qty, meal, username));         // saving outbox event handled here

        dietDay.addMeal(meal);

        return MealMapper.mapMealToMealDto(meal);
    }

    /*
        handling creating and saving OutboxEvent in this method
     */
    public ProductQuantityDto addProductQuantityToMeal(ProductQuantityDto productQuantityDto, Meal meal, String username) {
        ProductQuantity productQuantity = ProductQuantity.fromDto(productQuantityDto);

        ProductSnapshot productSnapshot = productSnapshotRepository.findById(productQuantityDto.getProductId())
                .orElseThrow(() -> new ProductSnapshotNotFoundException(productQuantityDto.getProductId()));

        productQuantity.setProductSnapshot(productSnapshot);
        productSnapshot.getProductQuantities().add(productQuantity);

        meal.addProductQuantity(productQuantity);

        // create OutboxEvent
        OutboxEvent event = eventFactory.create(new ProductAddedToMealEvent(
                productSnapshot.getName(),
                productSnapshot.getProductId(),
                productQuantity.getQuantity(),
                username
        ));

        outboxEventRepository.save(event);              // save OutboxEvent

        return ProductQuantityMapper.mapProductQuantityToProductQuantityDto(productQuantity);
    }
}
