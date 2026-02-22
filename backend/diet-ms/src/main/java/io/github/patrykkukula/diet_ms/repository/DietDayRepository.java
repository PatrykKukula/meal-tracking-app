package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.DietDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DietDayRepository extends JpaRepository<DietDay, Long> {
    @Query("SELECT DISTINCT d FROM DietDay d " +
            "JOIN FETCH d.meals m " +
            "JOIN FETCH m.productQuantities pq " +
            "JOIN FETCH pq.productSnapshot ps " +
            "WHERE d.dietDayId= :dietDayId")
    public DietDay fetchDietDay(@Param(value = "dietDayId") Long dietDayId);
}
