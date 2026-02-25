package io.github.patrykkukula.diet_ms.builder;

import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;

public class ProductQuantityTestBuilder {
    private Long id = 1L;
    private Double quantity = 5.0;
    private Meal meal = new Meal();
    private ProductSnapshot productSnapshot = new ProductSnapshot();

    private ProductQuantityTestBuilder() {};

    public static ProductQuantityTestBuilder productQuantity() {
        return new ProductQuantityTestBuilder();
    }

    public ProductQuantityTestBuilder quantity(Double quantity) {
        this.quantity = quantity;
        return this;
    }

    public ProductQuantityTestBuilder meal(Meal meal) {
        this.meal = meal;
        return this;
    }

    public ProductQuantityTestBuilder snapshot(ProductSnapshot snapshot) {
        this.productSnapshot = snapshot;
        return this;
    }

    public ProductQuantity build() {
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setProductQuantityId(id);
        productQuantity.setQuantity(quantity);
        productQuantity.setMeal(meal);
        productQuantity.setProductSnapshot(productSnapshot);
        return productQuantity;
    }
}
