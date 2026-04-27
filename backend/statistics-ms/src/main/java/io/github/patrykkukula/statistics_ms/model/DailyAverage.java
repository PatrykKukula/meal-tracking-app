package io.github.patrykkukula.statistics_ms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class DailyAverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long dailyAverageId;

    @Column(nullable = false)
    public Integer averageCalories;
    @Column(nullable = false)
    public Integer averageProtein;
    @Column(nullable = false)
    public Integer averageCarbs;
    @Column(nullable = false)
    public Integer averageFat;
    @Column(nullable = false)
    public Integer totalDaysWithDied;
}
