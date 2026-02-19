package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
}
