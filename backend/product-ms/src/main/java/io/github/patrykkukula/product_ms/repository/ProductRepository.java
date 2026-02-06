package io.github.patrykkukula.product_ms.repository;

import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p
            WHERE (:category IS NULL OR p.productCategory = :category)
            AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (p.ownerUsername IS NULL OR p.ownerUsername = :ownerUsername)
           """)
    public Page<Product> searchProducts(
            @Value("name") String name,
            @Value("category") ProductCategory category,
            @Value("ownerUsername") String ownerUsername,
            Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.ownerUsername = :username")
    public Long fetchCustomProductsAmountForUser(@Value("username") String username);
}

