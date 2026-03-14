package io.github.patrykkukula.diet_ms.controller;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.DietDayDtoRead;
import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.service.DietDayService;
import io.github.patrykkukula.mealtrackingapp_common.utils.BasicUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/diets", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class DietDayController {
    private final DietDayService dietDayService;

    @PostMapping
    public ResponseEntity<DietDayDtoRead> createDietDay(@Valid @RequestBody DietDayDto dietDayDto, HttpServletRequest request) {
        DietDayDtoRead dietDay = dietDayService.createDietDay(dietDayDto);

        return ResponseEntity.created(BasicUtils.setLocation(dietDay.dietDayId(), request)).body(dietDay);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietDayDtoRead> getDietDayById(@PositiveOrZero(message = "Id cannot be less than 0") @PathVariable Long id) {
        return ResponseEntity.ok(dietDayService.getDietDayById(id));
    }

    @GetMapping
    public ResponseEntity<List<DietDayDtoRead>> getDietDayListForUserForGivenYearAndMonth(
            @RequestParam(value = "year", required = false) int year,
            @Min(value = 1, message = "min month is 1") @Max(value = 12, message = "max month is 12")
            @RequestParam(value = "month") int month) {
        List<DietDayDtoRead> dietDays = dietDayService.getDietDayListForUserByGivenYearAndMonth(year, month);

        return ResponseEntity.ok(dietDays);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeDietDay(@PositiveOrZero(message = "Id cannot be less than 0") @PathVariable Long id) {
        dietDayService.removeDietDay(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/add_meal")
    public ResponseEntity<MealDto> addMealToDietDay(@PositiveOrZero(message = "Id cannot be less than 0") @PathVariable Long id,
                                                    @Valid @RequestBody MealDto mealDto) {
        return ResponseEntity.accepted().body(dietDayService.addMealToDietDay(id, mealDto));
    }
}
