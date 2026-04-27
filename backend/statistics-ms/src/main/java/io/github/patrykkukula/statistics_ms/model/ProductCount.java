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
//    @Enumerated(value = EnumType.STRING)
//    @Column(nullable = false)
//    public ProductCategory productCategory;
    @Column(nullable = false)
    public Integer usageCount;
    @Column(nullable = false)
    public Integer totalQuantity;
    @Column(nullable = false)
    public Double averageQuantity;

    public void setUsageCount(Integer count) {
        this.usageCount += count;
    }

    public void setAverageQuantity() {
        this.averageQuantity = (double) totalQuantity / usageCount;
    }

    public void setTotalQuantity(Integer quantity) {
        this.totalQuantity += quantity;
    }
}
