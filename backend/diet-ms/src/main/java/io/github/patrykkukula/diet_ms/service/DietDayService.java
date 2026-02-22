package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.assembler.DietDayAssembler;
import io.github.patrykkukula.diet_ms.dto.*;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.repository.DietDayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietDayService {
    private final DietDayRepository dietDayRepository;
    private final ProductSnapshotService productSnapshotService;
    private final DietDayAssembler dietDayAssembler;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public DietDayDtoRead createDietDay(DietDayDto dietDayDto) {
        DietDay dietDay = dietDayAssembler.assemble(dietDayDto);

        DietDay savedDiet = dietDayRepository.save(dietDay);

        return new DietDayDtoRead(savedDiet.getDietDayId(), dietDay.getOwnerUsername(), setProductsForMeal(dietDay));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public DietDayDtoRead getDietDayById(Long dietDayId) {
        DietDay dietDay = dietDayRepository.fetchDietDay(dietDayId);

        return new DietDayDtoRead(dietDayId, dietDay.getOwnerUsername(), setProductsForMeal(dietDay));
    }

    private List<MealDtoRead> setProductsForMeal(DietDay dietDay) {
        return dietDay.getMeals()
                .stream()
                .map(meal -> {
                    List<ProductDtoRead> products = productSnapshotService.getProductsForMeal(meal);
                    return new MealDtoRead(meal.getMealId(), meal.getName(), products);
                })
                .toList();
    };
}
