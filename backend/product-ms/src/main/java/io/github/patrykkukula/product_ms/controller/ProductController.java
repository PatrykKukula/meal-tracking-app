package io.github.patrykkukula.product_ms.controller;

import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.service.ProductService;
import io.github.patrykkukula.mealtrackingapp_common.utils.BasicUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "api/products", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductDto productDto, HttpServletRequest request) {
        ProductDto addedProduct = productService.addProduct(productDto);

        return ResponseEntity.created(BasicUtils.setLocation(addedProduct.getProductId(), request)).body(addedProduct);
    }

    @PostMapping("/custom")
    public ResponseEntity<ProductDto> addCustomProduct(@Valid @RequestBody ProductDto productDto, HttpServletRequest request) {
        ProductDto addedProduct = productService.addCustomProduct(productDto);

        return ResponseEntity.created(BasicUtils.setLocation(addedProduct.getProductId(), request)).body(addedProduct);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable @Min(value = 1, message = "Product ID cannot be less than 1")
                                                      Long productId) {
        return ResponseEntity.ok(productService.findProductById(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> findProducts(@RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
                                                         @RequestParam(name = "category", required = false) ProductCategory category,
                                                         @RequestParam(name = "name", defaultValue = "") String name) {
        return ResponseEntity.ok(productService.findProducts(pageNo, category, name));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto,
                                                    @PathVariable @Min(value = 1, message = "Product ID cannot be less than 1")
                                                    Long productId) {
        ProductDto updatedProduct = productService.updateProduct(productDto, productId);

        return ResponseEntity.accepted().body(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }
}
