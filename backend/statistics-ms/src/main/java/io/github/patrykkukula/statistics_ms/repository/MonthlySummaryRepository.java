package io.github.patrykkukula.statistics_ms.repository;

import io.github.patrykkukula.statistics_ms.model.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {
}
