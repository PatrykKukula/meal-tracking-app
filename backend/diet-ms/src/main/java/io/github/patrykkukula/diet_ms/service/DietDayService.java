package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.assembler.DietDayAssembler;
import io.github.patrykkukula.diet_ms.dto.*;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.repository.DietDayRepository;
import io.github.patrykkukula.diet_ms.security.AuthenticationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietDayService {
    private final DietDayRepository dietDayRepository;
    private final ProductSnapshotService productSnapshotService;
    private final DietDayAssembler dietDayAssembler;
    private final AuthenticationUtils authenticationUtils;

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

        isResourceOwner(dietDay);

        return new DietDayDtoRead(dietDayId, dietDay.getOwnerUsername(), setProductsForMeal(dietDay));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void removeDietDay(Long dietDayId) {
        DietDay dietDay = dietDayRepository.fetchDietDay(dietDayId);

        isResourceOwner(dietDay);

        dietDayRepository.delete(dietDay);
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

    // check if DietDay belongs to user
    private void isResourceOwner(DietDay dietDay) {
        String username = authenticationUtils.getAuthenticatedUserUsername();

        if (!dietDay.getOwnerUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
