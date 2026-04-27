package io.github.patrykkukula.statistics_ms.controller;

import io.github.patrykkukula.statistics_ms.dto.MonthlySummaryDto;
import io.github.patrykkukula.statistics_ms.dto.MostUsedProductDto;
import io.github.patrykkukula.statistics_ms.dto.TotalProductsDto;
import io.github.patrykkukula.statistics_ms.dto.WeeklySummaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Valid
@RequestMapping(value = "api/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StatisticsController {

    @GetMapping("/weekly-summary")
    public WeeklySummaryDto getWeeklyStatistics() {
        return null;
    }

    @GetMapping("/total-products")
    public TotalProductsDto getTotalProducts() {
        return null;
    }

    @GetMapping("/average-daily")
    public TotalProductsDto getDailyAverage() {
        return null;
    }

    @GetMapping("/most-used-products")
    public List<MostUsedProductDto> getMostUsedProducts() {
        return null;
    }

    @GetMapping("/monthly")
    public MonthlySummaryDto getMonthlySummary() {
        return null;
    }
}
