package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.assembler.DietDayAssembler;
import io.github.patrykkukula.diet_ms.cache.CacheUtils;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.exception.MealNotFoundException;
import io.github.patrykkukula.diet_ms.factory.OutboxEventFactory;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.repository.MealRepository;
import io.github.patrykkukula.diet_ms.security.AuthenticationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final AuthenticationUtils authenticationUtils;
    private final DietDayAssembler dietDayAssembler;
    private final CacheUtils cacheUtils;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void removeMeal(Long mealId) {
        Meal meal = fetchMeal(mealId);

        isResourceOwner(meal);

        DietDay dietDay = meal.getDietDay();

        dietDay.removeMeal(meal);

        cacheUtils.evictCaches(dietDay);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ProductQuantityDto addProductQuantityToMeal(Long mealId, ProductQuantityDto productQuantityDto) {
        Meal meal = fetchMeal(mealId);

        isResourceOwner(meal);

        cacheUtils.evictCaches(meal.getDietDay());

        return dietDayAssembler.addProductQuantityToMeal(productQuantityDto, meal, authenticationUtils.getAuthenticatedUserUsername());
    }

    private Meal fetchMeal(Long mealId) {
        return mealRepository.findByIdWithDietDay(mealId).orElseThrow(() -> new MealNotFoundException(mealId));
    }

    // check if Meal belongs to user
    private void isResourceOwner(Meal meal) {
        String username = authenticationUtils.getAuthenticatedUserUsername();

        if (!meal.getDietDay().getOwnerUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
