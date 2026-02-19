package io.github.patrykkukula.diet_ms.repository;

import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductQuantityRepository extends JpaRepository<ProductQuantity, Long> {
}
