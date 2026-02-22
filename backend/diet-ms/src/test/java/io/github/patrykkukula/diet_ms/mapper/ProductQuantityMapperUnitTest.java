package io.github.patrykkukula.diet_ms.mapper;

import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductQuantityMapperUnitTest {
    private ProductQuantity productQuantity = new ProductQuantity();

    @BeforeEach
    public void setUp() {
        productQuantity.setQuantity(2.0);
    }

    @Test
    @DisplayName("should map ProductQuantity to ProductQuantityDto correctly")
    public void shouldMapProductQuantityToProductQuantityDtoCorrectly() {
        ProductQuantityDto mappedQuantity = ProductQuantityMapper.mapProductQuantityToProductQuantityDto(productQuantity);

        Assertions.assertEquals(2.0, mappedQuantity.getQuantity());
    }

}
