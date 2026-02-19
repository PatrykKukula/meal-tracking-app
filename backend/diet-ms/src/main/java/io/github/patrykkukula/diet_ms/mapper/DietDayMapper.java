package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.model.DietDay;

public class DietDayMapper {
    private DietDayMapper() {}


    public static DietDayDto mapDietDayToDietDayDto(DietDay dietDay) {
        DietDayDto dietDayDto = new DietDayDto();
        dietDayDto.setDate(dietDayDto.getDate());
        dietDayDto.setMeals(dietDay.getMeals()
                .stream()
                .map(MealMapper::mapMealToMealDto)
                .toList());

        return dietDayDto;
    }


//    public static DietDay mapDietDayDtoDoDietDay(DietDayDto dietDayDto, String ownerUsername) {
//        DietDay dietDay = new DietDay();
//        dietDay.setDate(dietDayDto.getDate());
//        dietDay.setMeals(dietDayDto.getMeals()
//                .stream()
//                .map(MealMapper::mapMealDtoToMeal)
//                .toList());
//        dietDay.setOwnerUsername(ownerUsername);
//        return dietDay;
//    }
//
}
