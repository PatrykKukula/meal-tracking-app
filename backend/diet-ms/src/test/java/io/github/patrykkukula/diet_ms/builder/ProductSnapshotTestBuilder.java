package io.github.patrykkukula.diet_ms.builder;

import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class ProductSnapshotTestBuilder {
    private Long id = 1L;
    private String name = "snapshot";
    private ProductCategory productCategory = ProductCategory.FISH;
    private Integer calories = 100;
    private Integer protein = 100;
    private Integer carbs = 100;
    private Integer fat = 100;
    private String ownerUsername = "user";
    private List<ProductQuantity> productQuantities = new ArrayList<>();

    private ProductSnapshotTestBuilder() {}

    public static ProductSnapshotTestBuilder productSnapshot () {
        return new ProductSnapshotTestBuilder();
    }

    public ProductSnapshotTestBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductSnapshotTestBuilder productCategory(ProductCategory category) {
        this.productCategory = category;
        return this;
    }

    public ProductSnapshotTestBuilder calories(Integer calories) {
        this.calories = calories;
        return this;
    }

    public ProductSnapshotTestBuilder protein(Integer protein) {
        this.protein = protein;
        return this;
    }

    public ProductSnapshotTestBuilder carbs(Integer carbs) {
        this.carbs = carbs;
        return this;
    }

    public ProductSnapshotTestBuilder fat(Integer fat) {
        this.fat = fat;
        return this;
    }

    public ProductSnapshotTestBuilder ownerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
        return this;
    }

    public ProductSnapshotTestBuilder productQuantities(List<ProductQuantity> quantities) {
        this.productQuantities = quantities;
        return this;
    }

    public ProductSnapshot build() {
        ProductSnapshot snapshot = new ProductSnapshot();
        snapshot.setProductId(id);
        snapshot.setName(name);
        snapshot.setProductCategory(productCategory);
        snapshot.setCalories(calories);
        snapshot.setProtein(protein);
        snapshot.setCarbs(carbs);
        snapshot.setFat(fat);
        snapshot.setOwnerUsername(ownerUsername);
        snapshot.setProductQuantities(productQuantities);
        return snapshot;
    }

}
