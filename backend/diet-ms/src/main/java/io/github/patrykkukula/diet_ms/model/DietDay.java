package io.github.patrykkukula.diet_ms.model;

import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
public class DietDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dietDayId;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dietDay")
    @OrderBy(value = "orderIndex ASC")
    private Set<Meal> meals = new HashSet<>();

    @Column(nullable = false, length = 64)
    private String ownerUsername;

    public void addMeal(Meal meal) {
        meals.add(meal);
        meal.setDietDay(this);
    }

    public static DietDay fromDto(DietDayDto dto) {
        DietDay dietDay = new DietDay();
        dietDay.setDate(dto.getDate());
        return dietDay;
    }
}
