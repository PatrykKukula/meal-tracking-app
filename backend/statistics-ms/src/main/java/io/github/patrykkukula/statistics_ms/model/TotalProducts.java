package io.github.patrykkukula.statistics_ms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TotalProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long totalProductsId;

    @Column(nullable = false)
    public Integer totalProducts;
    @Column(nullable = false)
    public Integer globalProducts;
    @Column(nullable = false)
    public Integer customProducts;
}
