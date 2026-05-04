package io.github.patrykkukula.product_ms.service;

import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.model.Product;
import io.github.patrykkukula.product_ms.repository.ProductRepository;
import io.github.patrykkukula.product_ms.security.AuthenticationUtilsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceCacheTest {
    @MockitoBean
    private AuthenticationUtilsImpl authenticationUtilsImpl;
    @MockitoBean
    private StreamBridge streamBridge;
    @MockitoBean
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CacheManager cacheManager;

    private ProductDto productDto;
    private Product product = new Product();
    private Cache cache;

    @BeforeEach
    public void setUp() {
        productDto = ProductDto.builder()
                .name("product1")
                .productCategory(ProductCategory.FISH)
                .calories(100)
                .protein(100)
                .carbs(100)
                .fat(100)
                .build();
        product.setProductId(1L);
        cache = cacheManager.getCache("product");
    }

    @AfterEach
    public void clearCache() {
        Cache cache = cacheManager.getCache("product");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("Should cache find product by id correctly")
    @WithAnonymousUser                                                                                // not testing security here so no JWT
    public void shouldCacheFindProductByIdCorrectly() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertNull(cache.get(1L));

        ProductDto cachedProduct = productService.findProductById(product.getProductId());              // should call repository

        assertNotNull(cache.get(cachedProduct.getProductId()));

        productService.findProductById(cachedProduct.getProductId());                                   // should not call repository but cache

        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should evict cache correctly when update product")
    @WithMockUser(roles = "ADMIN", username = "admin")                                                // not testing security here so no JWT
    public void shouldEvictCacheCorrectlyWhenUpdateProduct() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(authenticationUtilsImpl.getAuthenticatedUserUsername()).thenReturn("admin");

        assertNull(cache.get(product.getProductId()));

        ProductDto cachedProduct = productService.findProductById(product.getProductId());              // repository call, cache PUT

        assertNotNull(cache.get(cachedProduct.getProductId()));

        productService.updateProduct(productDto, cachedProduct.getProductId());                         // cache EVICT, 2nd repositoru call

        assertNull(cache.get(cachedProduct.getProductId()));

        productService.findProductById(product.getProductId());                                         // cache empty, 3rd repository call

        verify(productRepository, times(3)).findById(anyLong());
    }

    @Test
    @DisplayName("Should evict cache correctly when delete product")
    @WithMockUser(roles = "ADMIN", username = "admin")                                                // not testing security here so no JWT
    public void shouldEvictCacheCorrectlyWhenDeleteProduct() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(authenticationUtilsImpl.getAuthenticatedUserUsername()).thenReturn("admin");

        assertNull(cache.get(1L));

        ProductDto cachedProduct = productService.findProductById(product.getProductId());          // repository call, cache PUT

        assertNotNull(cache.get(cachedProduct.getProductId()));

        productService.deleteProduct(cachedProduct.getProductId());                                 // cache EVICT, 2nd repository call

        assertNull(cache.get(cachedProduct.getProductId()));

        productService.findProductById(product.getProductId());                                      // cache empty, 3rd repository call

        verify(productRepository, times(3)).findById(anyLong());
    }
}
