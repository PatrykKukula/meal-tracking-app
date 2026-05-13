package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, Long> {
    @Query("SELECT q FROM ProductQuantity q JOIN q.meal m WHERE m.mealId= :mealId")
    public List<ProductQuantity> getProductQuantitiesForMeal(@Param(value = "mealId") Long mealId);

    @Query("""
            SELECT pq FROM ProductQuantity pq 
            JOIN FETCH pq.meal m 
            JOIN FETCH m.dietDay 
            WHERE pq.quantityId = :quantityId
            """)
    public Optional<ProductQuantity> findByIdWithMealAndDietDay(@Param(value = "quantityId") Long id);
}
