package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;

}
