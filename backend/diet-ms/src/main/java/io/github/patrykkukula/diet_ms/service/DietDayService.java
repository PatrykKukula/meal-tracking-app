package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.assembler.DietDayAssembler;
import io.github.patrykkukula.diet_ms.cache.CacheUtils;
import io.github.patrykkukula.diet_ms.dto.*;
import io.github.patrykkukula.diet_ms.exception.DietDayNotFoundException;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.repository.DietDayRepository;
import io.github.patrykkukula.mealtrackingapp_common.security.AuthenticationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietDayService {
    private final DietDayRepository dietDayRepository;
    private final ProductSnapshotService productSnapshotService;
    private final DietDayAssembler dietDayAssembler;
    private final AuthenticationUtils authenticationUtilsImpl;
    private final CacheUtils cacheUtils;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @CacheEvict(value = "monthlyDiets",
            key = "#result.date.getYear() + '-' + #result.date.getMonthValue() + '-' + @authenticationUtils.getAuthenticatedUserUsername()")
    public DietDayDtoRead createDietDay(DietDayDto dietDayDto) {
        DietDay dietDay = dietDayAssembler.assemble(dietDayDto);

        DietDay savedDiet = dietDayRepository.save(dietDay);

        return new DietDayDtoRead(savedDiet.getDietDayId(), savedDiet.getOwnerUsername(), savedDiet.getDate(), setProductsForMeal(dietDay));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Cacheable(value = "dietDay", key = "#dietDayId + '-' + @authenticationUtils.getAuthenticatedUserUsername()")
    public DietDayDtoRead getDietDayById(Long dietDayId) {
        DietDay dietDay = dietDayRepository.fetchDietDay(dietDayId).orElseThrow(() -> new DietDayNotFoundException(dietDayId));

        isResourceOwner(dietDay);

        return new DietDayDtoRead(dietDay.getDietDayId(), dietDay.getOwnerUsername(), dietDay.getDate(), setProductsForMeal(dietDay));
    }

    /**
     *
     * @param year - year to fetch DietDays for, provided from UI request
     * @param month - month in a given year to fetch DietDays for, provided from UI request
     * @return List of DietDayDtoRead
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Cacheable(value = "monthlyDiets", key = "#year + '-' + #month + '-' + @authenticationUtils.getAuthenticatedUserUsername()")
    public List<DietDayDtoRead> getDietDayListForUserByGivenYearAndMonth(int year, int month) {
        if (year <= 2021 || year >= LocalDate.now().getYear() + 5) {
            throw new IllegalArgumentException("Year must be at least 2021 and cannot be more than current year plus 5 years");
        }
        String username = authenticationUtilsImpl.getAuthenticatedUserUsername();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        return dietDayRepository.fetchDietDaysForUserForGivenYearAndMonth(startDate, endDate, username)
                .stream()
                .map(dietDay -> {
                    return new DietDayDtoRead(dietDay.getDietDayId(), dietDay.getOwnerUsername(), dietDay.getDate(), setProductsForMeal(dietDay));
                })
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @CacheEvict(value = "dietDay", key = "#dietDayId + '-' + @authenticationUtils.getAuthenticatedUserUsername()")
    public void removeDietDay(Long dietDayId) {
        DietDay dietDay = dietDayRepository.fetchDietDay(dietDayId).orElseThrow(() -> new DietDayNotFoundException(dietDayId));

        isResourceOwner(dietDay);

        dietDayRepository.delete(dietDay);

        cacheUtils.evictMonthlyDietsCache(dietDay);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @CacheEvict(value = "dietDay", key = "#dietDayId + '-' + @authenticationUtils.getAuthenticatedUserUsername()")
    public MealDto addMealToDietDay(Long dietDayId, MealDto mealDto) {
        DietDay dietDay = dietDayRepository.findById(dietDayId).orElseThrow(() -> new DietDayNotFoundException(dietDayId));

        isResourceOwner(dietDay);

        cacheUtils.evictMonthlyDietsCache(dietDay);

        return dietDayAssembler.addMealToDietDay(mealDto, dietDay, dietDay.getOwnerUsername());
    }

    /*
        set products for meal
     */
    private List<MealDtoRead> setProductsForMeal(DietDay dietDay) {
        return dietDay.getMeals()
                .stream()
                .map(meal -> {
                    List<ProductDtoRead> products = productSnapshotService.getProductsForMeal(meal);
                    return new MealDtoRead(meal.getMealId(), meal.getName(), products);
                })
                .toList();
    }

    // check if DietDay belongs to user
    private void isResourceOwner(DietDay dietDay) {
        String username = authenticationUtilsImpl.getAuthenticatedUserUsername();

        if (!dietDay.getOwnerUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
