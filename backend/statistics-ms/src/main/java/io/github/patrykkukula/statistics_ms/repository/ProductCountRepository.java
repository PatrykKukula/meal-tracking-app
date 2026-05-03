package io.github.patrykkukula.statistics_ms.repository;

import io.github.patrykkukula.statistics_ms.model.ProductCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCountRepository extends JpaRepository<ProductCount, Long> {
}
