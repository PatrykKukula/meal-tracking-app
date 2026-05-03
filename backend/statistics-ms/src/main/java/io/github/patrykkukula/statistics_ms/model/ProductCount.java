package io.github.patrykkukula.statistics_ms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long productCountId;

    @Column(nullable = false)
    public String productName;
    @Column(nullable = false)
    public Long productId;
    @Column(nullable = false)
    public String username;
    @Column(nullable = false)
    public Integer usageCount;
    @Column(nullable = false)
    public Integer totalQuantity;

    public void setUsageCount(Integer count) {
        this.usageCount += count;
    }

    public void setTotalQuantity(Integer quantity) {
        this.totalQuantity += quantity;
    }
}
