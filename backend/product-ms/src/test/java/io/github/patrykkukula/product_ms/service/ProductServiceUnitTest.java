package io.github.patrykkukula.product_ms.service;

import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.exception.CustomProductAmountExceededException;
import io.github.patrykkukula.product_ms.exception.ProductNotFoundException;
import io.github.patrykkukula.product_ms.model.Product;
import io.github.patrykkukula.product_ms.repository.ProductRepository;
import io.github.patrykkukula.product_ms.security.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {
    private ProductDto productDto;
    private Product product;
    private Product product2;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private AuthenticationUtils authenticationUtils;
    @InjectMocks
    private ProductService productService;

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
                .build();

        product = Product.builder()
                .productId(2L)
                .name("product2")
                .productCategory(ProductCategory.CEREAL)
                .calories(0)
                .protein(0)
                .carbs(0)
                .fat(0)
                .build();

        product2 = Product.builder()
                .productId(3L)
                .name("product3")
                .productCategory(ProductCategory.FRUITS)
                .calories(1)
                .protein(1)
                .carbs(1)
                .fat(1)
                .build();
    }

    @Test
    @DisplayName("Should add product correctly")
    public void shouldAddProductCorrectly() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto addedProduct = productService.addProduct(productDto);

        assertEquals("product2", addedProduct.getName());
        assertEquals(ProductCategory.CEREAL, addedProduct.getProductCategory());
    }

    @Test
    @DisplayName("Should add custom product correctly")
    public void shouldAddCustomProductCorrectly() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productRepository.fetchCustomProductsAmountForUser(anyString())).thenReturn(1L);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto addedProduct = productService.addCustomProduct(productDto);

        assertEquals("product2", addedProduct.getName());
        assertEquals(ProductCategory.CEREAL, addedProduct.getProductCategory());
    }

    @Test
    @DisplayName("Should set username correctly when add custom product correctly")
    public void shouldSetUsernameCorrectlyWhenAddCustomProductCorrectly() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productRepository.fetchCustomProductsAmountForUser(anyString())).thenReturn(1L);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        productService.addCustomProduct(productDto);

        verify(productRepository, times(1)).save(captor.capture());
        assertEquals("user", captor.getValue().getOwnerUsername());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when add custom product and user is not authenticated")
    public void shouldThrowAccessDeniedExceptionWhenAddCustomProductAndUserIsNotAuthenticated() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenThrow(AccessDeniedException.class);

        assertThrows(AccessDeniedException.class, () -> productService.addCustomProduct(productDto));
    }

    @Test
    @DisplayName("Should throw CustomProductAmountExceededException when add custom product and user is not authenticated")
    public void shouldThrowCustomProductAmountExceededExceptionWhenAddCustomProductAndUserIsNotAuthenticated() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productRepository.fetchCustomProductsAmountForUser(anyString())).thenReturn(101L);

        assertThrows(CustomProductAmountExceededException.class, () -> productService.addCustomProduct(productDto));
    }

    @Test
    @DisplayName("Should find product by ID correctly with null username")
    public void shouldFindProductByIdCorrectlyWithNullUsername() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductDto fetchedProduct = productService.findProductById(1L);

        assertEquals("product2", fetchedProduct.getName());
        verifyNoInteractions(authenticationUtils);
    }

    @Test
    @DisplayName("Should find product by ID correctly with valid username")
    public void shouldFindProductByIdCorrectlyWithValidUsername() {
        product.setOwnerUsername("user");
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");

        ProductDto fetchedProduct = productService.findProductById(1L);

        assertEquals("product2", fetchedProduct.getName());
        assertEquals("user", fetchedProduct.getOwnerUsername());
        verify(authenticationUtils, times(1)).getAuthenticatedUserUsername();
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when find product by ID and product not found")
    public void shouldThrowProductNotFoundExceptionWhenFindProductByIdAndProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findProductById(anyLong()));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when find product by ID and username does not match")
    public void shouldThrowAccessDeniedExceptionWhenFindProductByIdAndUsernameDoesNotMatch() {
        product.setOwnerUsername("user");
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("other user");

        assertThrows(AccessDeniedException.class, () -> productService.findProductById(anyLong()));
    }

    @Test
    @DisplayName("Should find products correctly")
    public void shouldFindProductsCorrectly() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenThrow(AccessDeniedException.class);
        when(productRepository.searchProducts(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(product, product2)));

        List<ProductDto> products = productService.findProducts(1, ProductCategory.CEREAL, "");

        assertEquals(2L, products.size());
        assertEquals("product2", products.getFirst().getName());
        assertEquals("product3", products.get(1).getName());
    }

    @Test
    @DisplayName("Should pass correct parameters to find products with no authentication")
    public void shouldPassCorrectParametersToFindProductsWithNoAuthentication() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenThrow(AccessDeniedException.class);
        when(productRepository.searchProducts(any(), any(), any(), any())).thenReturn(Page.empty());

        List<ProductDto> products = productService.findProducts(1, null, "");

        verify(productRepository, times(1)).searchProducts(
                eq(""),
                eq(null),
                eq(null),
                any(Pageable.class));
    }

    @Test
    @DisplayName("Should pass correct parameters to find products with authentication")
    public void shouldPassCorrectParametersToFindProductsWithAuthentication() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productRepository.searchProducts(any(), any(), any(), any())).thenReturn(Page.empty());

        List<ProductDto> products = productService.findProducts(1, ProductCategory.CEREAL, "");

        verify(productRepository, times(1)).searchProducts(
                eq(""),
                eq(ProductCategory.CEREAL),
                eq("user"),
                any(Pageable.class));
    }

    @Test
    @DisplayName("Should pass correct pageable to find products")
    public void shouldPassCorrectPageableToFindProducts() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productRepository.searchProducts(any(), any(), any(), any())).thenReturn(Page.empty());

        List<ProductDto> products = productService.findProducts(1, null, "");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(productRepository, times(1)).searchProducts(
                eq(""),
                eq(null),
                eq("user"),
                pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(1, pageable.getPageNumber());
        assertEquals(50, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("name").getDirection());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when find products with negative page number")
    public void shouldThrowIllegalArgumentExceptionWhenFindProductsWithNegativePageNumber() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> productService.findProducts(-1, null, ""));
        assertEquals("Page number cannot be less than 0", ex.getMessage());
    }

    @Test
    @DisplayName("Should update product correctly")
    public void shouldUpdateProductCorrectly(){
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(authenticationUtils.canUserModifyProduct(any(Product.class))).thenReturn(true);

        ProductDto updatedProduct = productService.updateProduct(productDto, 1L);

        assertEquals("product1", updatedProduct.getName());
        assertEquals(ProductCategory.FISH, updatedProduct.getProductCategory());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when update product and user cannot modify")
    public void shouldThrowAccessDeniedExceptionWhenUpdateProductAndUserCannotModify(){
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(authenticationUtils.canUserModifyProduct(any(Product.class))).thenThrow(AccessDeniedException.class);

        assertThrows(AccessDeniedException.class, () -> productService.updateProduct(productDto, 1L));
    }

    @Test
    @DisplayName("Should delete product correctly")
    public void shouldDeleteProductCorrectly(){
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(authenticationUtils.canUserModifyProduct(any(Product.class))).thenReturn(true);
        doNothing().when(productRepository).deleteById(anyLong());

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when delete product and user cannot modify")
    public void shouldThrowAccessDeniedExceptionWhenDeleteProductAndUserCannotModify(){
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(authenticationUtils.canUserModifyProduct(any(Product.class))).thenThrow(AccessDeniedException.class);

        assertThrows(AccessDeniedException.class, () -> productService.updateProduct(productDto, 1L));
        verify(productRepository, times(0)).deleteById(1L);
    }
}
