package io.github.patrykkukula.diet_ms.controller;

import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.service.MealService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{id}/add_quantity")
    public ResponseEntity<ProductQuantityDto> addProductQuantityToMeal(@PositiveOrZero(message = "Id cannot be less than 0") @PathVariable Long id,
                                                                       @RequestBody ProductQuantityDto productQuantityDto) {
        return ResponseEntity.accepted().body(mealService.addProductQuantityToMeal(id, productQuantityDto));
    }
}
