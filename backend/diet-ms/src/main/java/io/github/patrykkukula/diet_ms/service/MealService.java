package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.exception.MealNotFoundException;
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

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void removeMeal(Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new MealNotFoundException(mealId));

        isResourceOwner(meal);

        DietDay dietDay = meal.getDietDay();
        dietDay.removeMeal(meal);
    }

    // check if Meal belongs to user
    private void isResourceOwner(Meal meal) {
        String username = authenticationUtils.getAuthenticatedUserUsername();

        if (!meal.getDietDay().getOwnerUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
