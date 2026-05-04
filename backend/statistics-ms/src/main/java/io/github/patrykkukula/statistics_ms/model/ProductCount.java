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
    private Long productCountId;

    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Long productId;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private Integer usageCount = 0;
    @Column(nullable = false)
    private Double totalQuantity = 0.0;

    public void incrementUsageCount() {
        this.usageCount += 1;
    }

    public void decrementUsageCount() {
        this.usageCount -= 1;
    }

    public void addTotalQuantity(Double quantity) {
        this.totalQuantity += quantity;
    }
}
