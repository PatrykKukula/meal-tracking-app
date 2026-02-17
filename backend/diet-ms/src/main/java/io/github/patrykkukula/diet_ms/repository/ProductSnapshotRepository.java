package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSnapshotRepository extends JpaRepository<ProductSnapshot, Long> {
}
