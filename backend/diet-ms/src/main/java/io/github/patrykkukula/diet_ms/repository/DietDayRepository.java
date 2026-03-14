package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.DietDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietDayRepository extends JpaRepository<DietDay, Long> {
    @Query("SELECT DISTINCT d FROM DietDay d " +
            "JOIN FETCH d.meals m " +
            "JOIN FETCH m.productQuantities pq " +
            "JOIN FETCH pq.productSnapshot ps " +
            "WHERE d.dietDayId= :dietDayId")
    public Optional<DietDay> fetchDietDay(@Param(value = "dietDayId") Long dietDayId);

    /**
     * @param startDate
     * @param endDate
     * @param username
     * @return list of DietDays for given user, and given month in a given year
     */
    @Query("SELECT d FROM DietDay d WHERE d.ownerUsername= :username" +
            " AND d.date>= :startDate" +
            " AND d.date< :endDate")
    public List<DietDay> fetchDietDaysForUserForGivenYearAndMonth(
            @Param(value = "startDate") LocalDate startDate,
            @Param(value = "endDate") LocalDate endDate,
            @Param(value = "username") String username);
}
