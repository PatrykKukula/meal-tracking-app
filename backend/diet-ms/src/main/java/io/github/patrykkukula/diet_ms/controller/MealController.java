package io.github.patrykkukula.diet_ms.controller;

import io.github.patrykkukula.diet_ms.service.MealService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/diets/meal")
@RequiredArgsConstructor
public class MealController {
    private final MealService mealService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMeal(@PositiveOrZero(message = "Id cannot be less than 0") @PathVariable Long id) {
        mealService.removeMeal(id);

        return ResponseEntity.noContent().build();
    }
}
