package io.github.patrykkukula.statistics_ms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MonthlySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long monthlySummaryId;

    @Column(nullable = false)
    public Integer year;
    @Column(nullable = false)
    public Integer month;
    @Column(nullable = false)
    public String monthName;
    @Column(nullable = false)
    public Integer totalCalories;
    @Column(nullable = false)
    public Integer totalProtein;
    @Column(nullable = false)
    public Integer totalCarbs;
    @Column(nullable = false)
    public Integer totalFat;
    @Column(nullable = false)
    public Integer averageCalories;
    @Column(nullable = false)
    public Integer averageProtein;
    @Column(nullable = false)
    public Integer averageCarbs;
    @Column(nullable = false)
    public Integer averageFat;
    @Column(nullable = false)
    public Integer daysWithDiet;
    @Column(nullable = false)
    public Integer totalDaysInMonth;

    public void setMonthName(){
        this.monthName = Month.of(this.month).toString();
    }
}
