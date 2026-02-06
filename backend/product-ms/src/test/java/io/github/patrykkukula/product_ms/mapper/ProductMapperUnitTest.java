package io.github.patrykkukula.product_ms.mapper;

import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductMapperUnitTest {
    private ProductDto productDto;
    private Product product;

    @BeforeEach
    public void setUp(){
        productDto = ProductDto.builder()
                .name("product1")
                .productCategory(ProductCategory.FISH)
                .calories(100)
                .protein(100)
                .carbs(100)
                .fat(100)
                .build();

        product = Product.builder()
                .name("product2")
                .productCategory(ProductCategory.CEREAL)
                .calories(0)
                .protein(0)
                .carbs(0)
                .fat(0)
                .build();

    }

    @Test
    @DisplayName("Should map ProductDto to Product correctly")
    public void shouldMapProductDtoToProductCorrectly(){
        Product mappedProduct = ProductMapper.mapProductDtoToProduct(productDto);

        assertEquals("product1", mappedProduct.getName());
        assertEquals(ProductCategory.FISH, mappedProduct.getProductCategory());
        assertEquals(100, mappedProduct.getCalories());
        assertEquals(100, mappedProduct.getProtein());
        assertEquals(100, mappedProduct.getCarbs());
        assertEquals(100, mappedProduct.getFat());
    }

    @Test
    @DisplayName("Should map Product to ProductDto correctly")
    public void shouldMapProductToProductDtoCorrectly(){
        ProductDto mappedProduct = ProductMapper.mapProductToProductDto(product);

        assertEquals("product2", mappedProduct.getName());
        assertEquals(ProductCategory.CEREAL, mappedProduct.getProductCategory());
        assertEquals(0, mappedProduct.getCalories());
        assertEquals(0, mappedProduct.getProtein());
        assertEquals(0, mappedProduct.getCarbs());
        assertEquals(0, mappedProduct.getFat());
    }

    @Test
    @DisplayName("Should map ProductDto to Product update correctly")
    public void shouldMapProductDtoToProductUpdate(){
        Product mappedProduct = ProductMapper.mapProductDtoToProductUpdate(productDto, product);

        assertEquals("product1", mappedProduct.getName());
        assertEquals(ProductCategory.FISH, mappedProduct.getProductCategory());
        assertEquals(100, mappedProduct.getCalories());
        assertEquals(100, mappedProduct.getProtein());
        assertEquals(100, mappedProduct.getCarbs());
        assertEquals(100, mappedProduct.getFat());
    }
}
