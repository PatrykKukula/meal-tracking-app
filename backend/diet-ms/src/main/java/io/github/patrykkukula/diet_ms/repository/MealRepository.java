package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    @Query("SELECT m FROM Meal m JOIN FETCH m.dietDay WHERE m.mealId= :mealId")
    public Optional<Meal> findByIdWithDietDay(Long mealId);
}
