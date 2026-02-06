package io.github.patrykkukula.product_ms.service;

import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.security.AuthenticationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceCacheTest {
    @MockitoBean
    private AuthenticationUtils authenticationUtils;
    @Autowired
    private ProductService productService;
    @Autowired
    private CacheManager cacheManager;

    private ProductDto productDto;
    private Cache cache;

    @BeforeEach
    public void setUp() {
        productDto = ProductDto.builder()
                .productId(1L)
                .name("product1")
                .productCategory(ProductCategory.FISH)
                .calories(100)
                .protein(100)
                .carbs(100)
                .fat(100)
                .ownerUsername(null)
                .build();

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
        assertNull(cache.get(1L));

        cache.put(1L, productDto);

        ProductDto cachedProduct = productService.findProductById(1L);

        assertNotNull(cache.get(1L));
    }

    @Test
    @DisplayName("Should evict cache correctly when update product")
    @WithMockUser(roles = "ADMIN", username = "admin")                                                // not testing security here so no JWT
    public void shouldEvictCacheCorrectlyWhenUpdateProduct() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("admin");
        assertNull(cache.get(1L));
        productService.addProduct(productDto);

        cache.put(1L, productDto);

        ProductDto cachedProduct = productService.findProductById(1L);
        assertNotNull(cache.get(1L));

        productService.updateProduct(productDto, 1L);
        assertNull(cache.get(1L));
    }

    @Test
    @DisplayName("Should evict cache correctly when delete product")
    @WithMockUser(roles = "ADMIN", username = "admin")                                                // not testing security here so no JWT
    public void shouldEvictCacheCorrectlyWhenDeleteProduct() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("admin");
        assertNull(cache.get(1L));
        productService.addProduct(productDto);

        cache.put(1L, productDto);

        ProductDto cachedProduct = productService.findProductById(1L);
        assertNotNull(cache.get(1L));

        productService.deleteProduct(1L);
        assertNull(cache.get(1L));
    }
}
