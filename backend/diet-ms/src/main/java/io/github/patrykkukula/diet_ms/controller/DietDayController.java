package io.github.patrykkukula.diet_ms.controller;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.DietDayDtoRead;
import io.github.patrykkukula.diet_ms.service.DietDayService;
import io.github.patrykkukula.utils.BasicUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
}
