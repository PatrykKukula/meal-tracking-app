package io.github.patrykkukula.product_ms.mapper;

import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.model.Product;

public class ProductMapper {
    public static Product mapProductDtoToProduct(ProductDto productDto){
        return Product.builder()
                .name(productDto.getName())
                .productCategory(productDto.getProductCategory())
                .calories(productDto.getCalories())
                .protein(productDto.getProtein())
                .carbs(productDto.getCarbs())
                .fat(productDto.getFat())
                .build();
    }

    public static ProductDto mapProductToProductDto(Product product){
        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .productCategory(product.getProductCategory())
                .calories(product.getCalories())
                .protein(product.getProtein())
                .carbs(product.getCarbs())
                .fat(product.getFat())
                .ownerUsername(product.getOwnerUsername())
                .build();
    }

    // assuming all fields will be provided from UI, validated for nulls at dto class
    public static Product mapProductDtoToProductUpdate(ProductDto productDto, Product product){
        product.setName(productDto.getName());
        product.setProductCategory(productDto.getProductCategory());
        product.setCalories(productDto.getCalories());
        product.setProtein(productDto.getProtein());
        product.setCarbs(productDto.getCarbs());
        product.setFat(productDto.getFat());
        return product;
    }
}
