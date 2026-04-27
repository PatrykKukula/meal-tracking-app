package io.github.patrykkukula.statistics_ms.repository;

import io.github.patrykkukula.statistics_ms.model.DailyAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyAverageRepository extends JpaRepository<DailyAverage, Long> {
}
