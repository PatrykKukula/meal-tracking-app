package io.github.patrykkukula.statistics_ms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class WeeklySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long weeklySummaryId;

    @Column(nullable = false)
    private LocalDate weekStart;
    @Column(nullable = false)
    private LocalDate weekEnd;
    @Column(nullable = false)
    public Integer totalCalories;
    @Column(nullable = false)
    public Integer averageCalories;
    @Column(nullable = false)
    public Integer totalProtein;
    @Column(nullable = false)
    public Integer daysLogged;
}
