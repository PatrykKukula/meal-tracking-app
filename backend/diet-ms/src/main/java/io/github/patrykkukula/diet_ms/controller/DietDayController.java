package io.github.patrykkukula.diet_ms.controller;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.service.DietDayService;
import io.github.patrykkukula.utils.BasicUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/diets", produces = MediaType.APPLICATION_JSON_VALUE)
public class DietDayController {
    private final DietDayService dietDayService;

    @PostMapping
    public ResponseEntity<DietDayDto> createDietDay(@RequestBody DietDayDto dietDayDto, HttpServletRequest request) {
        DietDayDto dietDay = dietDayService.createDietDay(dietDayDto);

        return ResponseEntity.created(BasicUtils.setLocation(dietDay.getDietDatId(), request)).body(dietDayDto);
    }
}
