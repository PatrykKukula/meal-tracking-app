package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.DietDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietDayRepository extends JpaRepository<DietDay, Long> {
}
