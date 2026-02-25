package io.github.patrykkukula.diet_ms.builder;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.MealDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DietDayDtoTestBuilder {
    private Long id = 1L;
    private LocalDate date = LocalDate.of(2000, 1, 1);
    private List<MealDto> meals = new ArrayList<>();

    private DietDayDtoTestBuilder() {}

    public static DietDayDtoTestBuilder dietDayDto() {
        return new DietDayDtoTestBuilder();
    }

    public DietDayDtoTestBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public DietDayDtoTestBuilder meals(List<MealDto> meals) {
        this.meals = meals;
        return this;
    }

    public DietDayDto build() {
        DietDayDto dietDayDto = new DietDayDto();
        dietDayDto.setDietDayId(id);
        dietDayDto.setDate(date);
        dietDayDto.setMeals(meals);
        return dietDayDto;
    }
}
