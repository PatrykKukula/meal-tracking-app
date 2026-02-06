package io.github.patrykkukula.product_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.exception.CustomProductAmountExceededException;
import io.github.patrykkukula.product_ms.exception.ProductNotFoundException;
import io.github.patrykkukula.product_ms.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private ProductService productService;

    private ProductDto productDto;
    private ProductDto productDto2;
    private ProductDto invalidProductDto;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAdmin;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser;

    @BeforeEach
    public void setUp() {
        jwtAdmin = SecurityMockMvcRequestPostProcessors
                .jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

        jwtUser = SecurityMockMvcRequestPostProcessors
                .jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));

        productDto = ProductDto.builder()
                .name("product1")
                .productCategory(ProductCategory.FISH)
                .calories(100)
                .protein(100)
                .carbs(100)
                .fat(100)
                .build();
        productDto2 = ProductDto.builder()
                .name("product2")
                .productCategory(ProductCategory.FISH)
                .calories(100)
                .protein(100)
                .carbs(100)
                .fat(100)
                .build();

        invalidProductDto = ProductDto.builder()
                .name("p".repeat(65))
                .productCategory(null)
                .calories(-1)
                .protein(-1)
                .carbs(-1)
                .fat(null)
                .build();
    }

    @Test
    @DisplayName("Should allow admin enter admin endpoints")
    public void shouldAllowAdminEnterAdminEndpoints() throws Exception {
        when(productService.addProduct(any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(post("/api/products")
                        .with(jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated());

        verify(productService, times(1)).addProduct(any(ProductDto.class));
    }

    @Test
    @DisplayName("Should allow admin enter authenticated endpoints")
    public void shouldAllowAdminEnterAuthenticatedEndpoints() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/api/products/{productId}", 1L)
                        .with(jwtAdmin))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should allow admin enter public endpoints")
    public void shouldAllowAdminEnterPublicEndpoints() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{productId}", 1L)
                        .with(jwtAdmin))
                .andExpect(status().isOk());

        verify(productService, times(1)).findProductById(1L);
    }

    @Test
    @DisplayName("Should respond 403 when user enter admin endpoints")
    public void shouldRespond403WhenUserEnterAdminEndpoints() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(jwtUser)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should allow user enter authenticated endpoints")
    public void shouldAllowUserEnterAuthenticatedEndpoints() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/api/products/{productId}", 1L)
                        .with(jwtUser))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should allow user enter public endpoints")
    public void shouldAllowUserEnterPublicEndpoints() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{productId}", 1L)
                        .with(jwtUser))
                .andExpect(status().isOk());

        verify(productService, times(1)).findProductById(1L);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 401 when anonymous enter admin endpoints")
    public void shouldRespond401WhenAnonymousEnterAdminEndpoints() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should respond 401 when anonymous enter authenticated endpoints")
    public void shouldRespond401WhenAnonymousEnterAuthenticatedEndpoints() throws Exception {
        mockMvc.perform(delete("/api/products/{productId}", 1L))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Should allow anonymous enter public endpoints")
    public void shouldAllowAnonymousEnterPublicEndpoints() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{productId}", 1L))
                .andExpect(status().isOk());

        verify(productService, times(1)).findProductById(1L);
    }

    @Test
    @DisplayName("Should contain correlation-id in response header")
    public void shouldContainCorrelationIdInResponseHeader() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{productId}", 1L)
                        .with(jwtUser))
                .andExpect(header().string("correlation-id", Matchers.not(Matchers.emptyString())));
    }

    @Test
    @DisplayName("Should add product correctly")
    public void shouldAddProductCorrectly() throws Exception {
        when(productService.addProduct(any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(post("/api/products")
                        .with(jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", Matchers.not(Matchers.emptyString())),
                        jsonPath("$.name").value("product1"),
                        jsonPath("$.calories").value(100),
                        jsonPath("$.protein").value(100),
                        jsonPath("$.carbs").value(100),
                        jsonPath("$.fat").value(100)
                );

        verify(productService, times(1)).addProduct(any(ProductDto.class));
    }

    @Test
    @DisplayName("Should add custom product correctly")
    public void shouldAddCustomProductCorrectly() throws Exception {
        when(productService.addCustomProduct(any(ProductDto.class))).thenReturn(productDto);

        mockMvc.perform(post("/api/products/custom")
                        .with(jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", Matchers.not(Matchers.emptyString())),
                        jsonPath("$.name").value("product1"),
                        jsonPath("$.calories").value(100),
                        jsonPath("$.protein").value(100),
                        jsonPath("$.carbs").value(100),
                        jsonPath("$.fat").value(100)
                );

        verify(productService, times(1)).addCustomProduct(any(ProductDto.class));
    }

    @Test
    @DisplayName("Should respond 400 when ProductDto in request body is invalid")
    public void shouldRespond400WhenProductDtoInRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/products/custom")
                        .with(jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidProductDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.message", Matchers.allOf(
                                Matchers.containsString("cannot be null"),
                                Matchers.containsString("cannot exceed"),
                                Matchers.containsString("cannot be less than 0")
                        )),
                        jsonPath("$.path").isNotEmpty(),
                        jsonPath("$.occurrenceTime").isNotEmpty()
                );

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should respond 400 when add custom product and product limit exceeded")
    public void shouldReturnErrorResponseCorrectlyWhenAddCustomProductAndProductLimitExceeded() throws Exception {
        when(productService.addCustomProduct(any(ProductDto.class))).thenThrow(new CustomProductAmountExceededException());

        mockMvc.perform(post("/api/products/custom")
                        .with(jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.message").value(Matchers.containsString("You can add at most 100 custom products")),
                        jsonPath("$.path").isNotEmpty(),
                        jsonPath("$.occurrenceTime").isNotEmpty()
                );

        verify(productService, times(1)).addCustomProduct(any(ProductDto.class));
    }

    @Test
    @DisplayName("Should find product by id correctly")
    public void shouldFindProductByIdCorrectly() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{productId}", 1L)
                        .with(jwtAdmin))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("product1"),
                        jsonPath("$.calories").value(100),
                        jsonPath("$.protein").value(100),
                        jsonPath("$.carbs").value(100),
                        jsonPath("$.fat").value(100)
                );

        verify(productService, times(1)).findProductById(eq(1L));
    }

    @Test
    @DisplayName("Should respond 400 when find product by id with negative id")
    public void shouldRespond400WhenFindProductByIdWithNegativeId() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", -1L)
                        .with(jwtAdmin))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.message").value("Product ID cannot be less than 1"),
                        jsonPath("$.path").isNotEmpty(),
                        jsonPath("$.occurrenceTime").isNotEmpty()
                );

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should respond 404 when find product by id and product not found")
    public void shouldRespond404WhenFindProductByIdAndProductNotFound() throws Exception {
        when(productService.findProductById(anyLong())).thenThrow(new ProductNotFoundException(1L));

        mockMvc.perform(get("/api/products/{productId}", 1L)
                        .with(jwtAdmin))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusMessage").value("Not Found"),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message", Matchers.containsString("not found")),
                        jsonPath("$.path").isNotEmpty(),
                        jsonPath("$.occurrenceTime").isNotEmpty()
                );

        verify(productService, times(1)).findProductById(eq(1L));
    }

    @Test
    @DisplayName("Should find products correctly")
    public void shouldFindProductsCorrectly() throws Exception {
        when(productService.findProducts(anyInt(), any(ProductCategory.class), anyString()))
                .thenReturn(List.of(productDto, productDto2));

        mockMvc.perform(get("/api/products")
                        .param("pageNo", "1")
                        .param("category", ProductCategory.FISH.toString())
                        .param("name", "")
                        .with(jwtAdmin))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size()").value(2),
                        jsonPath("$[0].name").value("product1"),
                        jsonPath("$[1].name").value("product2")
                );

        verify(productService, times(1)).findProducts(eq(1), eq(ProductCategory.FISH), eq(""));
    }

    @Test
    @DisplayName("Should find products correctly")
    public void shouldProvideCorrectParamsToServiceWithNoRequestParams() throws Exception {
        when(productService.findProducts(anyInt(), isNull(), anyString()))
                .thenReturn(List.of(productDto, productDto2));

        mockMvc.perform(get("/api/products")
                        .with(jwtAdmin))
                .andExpectAll(
                        status().isOk()
                );

        verify(productService, times(1)).findProducts(eq(0), isNull(), eq(""));
    }

    @Test
    @DisplayName("Should respond 400 when find products with negative page number")
    public void shouldRespond400WhensFindProductsWithNegativePageNumber() throws Exception {
        when(productService.findProducts(anyInt(), isNull(), anyString()))
                .thenThrow(new IllegalArgumentException("Page number cannot be less than 0"));

        mockMvc.perform(get("/api/products")
                        .param("pageNo", "-1")
                        .with(jwtAdmin))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.statusMessage").value("Bad Request"),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.message").value("Page number cannot be less than 0"),
                        jsonPath("$.path").isNotEmpty(),
                        jsonPath("$.occurrenceTime").isNotEmpty()
                );

        verify(productService, times(1)).findProducts(eq(-1), isNull(), eq(""));
    }

    @Test
    @DisplayName("Should update product correctly")
    public void shouldUpdateProductCorrectly() throws Exception {
        when(productService.updateProduct(any(ProductDto.class), anyLong())).thenReturn(productDto);

        mockMvc.perform(put("/api/products/{productId}", 1L)
                        .with(jwtAdmin)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.name").value("product1"),
                        jsonPath("$.calories").value(100),
                        jsonPath("$.protein").value(100),
                        jsonPath("$.carbs").value(100),
                        jsonPath("$.fat").value(100)
                );

        verify(productService, times(1)).updateProduct(any(ProductDto.class), eq(1L));
    }

    @Test
    @DisplayName("Should delete product correctly")
    public void shouldDeleteProductCorrectly() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/api/products/{productId}", 1L)
                        .with(jwtAdmin))
                .andExpectAll(
                        status().isNoContent()
                );

        verify(productService, times(1)).deleteProduct(eq(1L));
    }
}
