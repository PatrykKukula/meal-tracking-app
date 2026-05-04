package io.github.patrykkukula.statistics_ms.repository;

import io.github.patrykkukula.statistics_ms.model.ProductCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCountRepository extends JpaRepository<ProductCount, Long> {

    public Optional<ProductCount> findByProductIdAndUsername(Long productId, String username);

    @Query("SELECT pc FROM ProductCount pc WHERE pc.username =  :username")
    public List<ProductCount> findMostUsedProductsByUsername(@Param(value = "username") String username, Pageable pageable);
}
