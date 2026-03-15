package io.github.patrykkukula.product_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.exception.CustomProductAmountExceededException;
import io.github.patrykkukula.product_ms.exception.ProductNotFoundException;
import io.github.patrykkukula.product_ms.security.SecurityConfig;
import io.github.patrykkukula.product_ms.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {
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

    @Nested
    @DisplayName("securityTests")
    class securityTests {
        @Nested
        @DisplayName("when make request with ROLE_ADMIN")
        class whenMakeRequestWithAdminRole {
            @Test
            @DisplayName("Should allow enter ROLE_ADMIN secured endpoints")
            public void shouldAllowEnterRoleAdminSecuredEndpoints() throws Exception {
                when(productService.addProduct(any(ProductDto.class))).thenReturn(productDto);

                mockMvc.perform(post("/api/products")
                                .with(jwtAdmin)
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productDto)))
                        .andExpect(status().isCreated());

                verify(productService, times(1)).addProduct(any(ProductDto.class));
            }

            @Test
            @DisplayName("Should allow enter authenticated endpoints")
            public void shouldAllowEnterAuthenticatedEndpoints() throws Exception {
                doNothing().when(productService).deleteProduct(anyLong());

                mockMvc.perform(delete("/api/products/{productId}", 1L)
                                .with(jwtAdmin))
                        .andExpect(status().isNoContent());

                verify(productService, times(1)).deleteProduct(1L);
            }

            @Test
            @DisplayName("Should allow enter public endpoints")
            public void shouldAllowEnterPublicEndpoints() throws Exception {
                when(productService.findProductById(anyLong())).thenReturn(productDto);

                mockMvc.perform(get("/api/products/{productId}", 1L)
                                .with(jwtAdmin))
                        .andExpect(status().isOk());

                verify(productService, times(1)).findProductById(1L);
            }
        }

        @Nested
        @DisplayName("when make request with ROLE_USER")
        class whenMakeRequestWithRoleUser {
            @Test
            @DisplayName("should respond 403 when enter ROLE_ADMIN secured endpoints")
            public void shouldRespond403WhenEnterRoleAdminSecuredEndpoints() throws Exception {
                mockMvc.perform(post("/api/products")
                                .with(jwtUser)
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productDto)))
                        .andExpect(status().isForbidden());

                verifyNoInteractions(productService);
            }

            @Test
            @DisplayName("should allow enter authenticated endpoints")
            public void shouldAllowEnterAuthenticatedEndpoints() throws Exception {
                doNothing().when(productService).deleteProduct(anyLong());

                mockMvc.perform(delete("/api/products/{productId}", 1L)
                                .with(jwtUser))
                        .andExpect(status().isNoContent());

                verify(productService, times(1)).deleteProduct(1L);
            }

            @Test
            @DisplayName("should allow enter public endpoints")
            public void shouldUserEnterPublicEndpoints() throws Exception {
                when(productService.findProductById(anyLong())).thenReturn(productDto);

                mockMvc.perform(get("/api/products/{productId}", 1L)
                                .with(jwtUser))
                        .andExpect(status().isOk());

                verify(productService, times(1)).findProductById(1L);
            }
        }

        @Nested
        @DisplayName("when make request as ANONYMOUS")
        class whenMakeRequestAsAnonymous {
            @Test
            @WithAnonymousUser
            @DisplayName("Should respond 401 when enter ROLE_ADMIN secured endpoints")
            public void shouldRespond401WhenEnterRoleAdminSecuredEndpoints() throws Exception {
                mockMvc.perform(post("/api/products")
                                .contentType(APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productDto)))
                        .andExpect(status().isUnauthorized());

                verifyNoInteractions(productService);
            }

            @Test
            @WithAnonymousUser
            @DisplayName("Should respond 401 when enter authenticated endpoints")
            public void shouldRespond401WhenEnterAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete("/api/products/{productId}", 1L))
                        .andExpect(status().isUnauthorized());

                verifyNoInteractions(productService);
            }

            @Test
            @WithAnonymousUser
            @DisplayName("Should allow enter public endpoints")
            public void shouldAllowEnterPublicEndpoints() throws Exception {
                when(productService.findProductById(anyLong())).thenReturn(productDto);

                mockMvc.perform(get("/api/products/{productId}", 1L))
                        .andExpect(status().isOk());

                verify(productService, times(1)).findProductById(1L);
            }
        }
    }

    @Nested
    @DisplayName("validation tests")
    class validationTests {
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
                                    Matchers.containsString("is required"),
                                    Matchers.containsString("cannot exceed"),
                                    Matchers.containsString("cannot be less than 0")
                            )),
                            jsonPath("$.path").isNotEmpty(),
                            jsonPath("$.occurrenceTime").isNotEmpty()
                    );

            verifyNoInteractions(productService);
        }

        @Test
        @DisplayName("Should respond 400 when path variable ID is negative")
        public void shouldRespond400WhenPathVariableIdIsNegative() throws Exception {
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
    }

    @Nested
    @DisplayName("when addProduct")
    class whenAddProduct {
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
    }

    @Nested
    @DisplayName("when addCustomProduct")
    class whenAddCustomProduct {
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
        @DisplayName("Should respond 400 when product limit exceeded")
        public void shouldRespond400WhenProductLimitExceeded() throws Exception {
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
    }

    @Nested
    @DisplayName("when findProductById")
    class whenFindProductById {
        @Test
        @DisplayName("Should find product correctly")
        public void shouldFindProductCorrectly() throws Exception {
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
        @DisplayName("Should respond 404 when product not found")
        public void shouldRespond404WhenProductNotFound() throws Exception {
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
    }

    @Nested
    @DisplayName("when findProducts")
    class whenFindProducts {
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
        @DisplayName("Should provide correct parameters to service with not request params")
        public void shouldProvideCCorrectParametersToServiceWithNotRequestParams() throws Exception {
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
        @DisplayName("Should respond 400 when page number is negative")
        public void shouldRespond400WhenPageNumberIsNegative() throws Exception {
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
    }

    @Nested
    @DisplayName("when updateProduct")
    class whenUpdateProduct {
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
    }

    @Nested
    @DisplayName("when deleteProduct")
    class whenDeleteProduct {
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

    @Test
    @DisplayName("Should contain correlation-id in response header")
    public void shouldContainCorrelationIdInResponseHeader() throws Exception {
        when(productService.findProductById(anyLong())).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{productId}", 1L)
                        .with(jwtUser))
                .andExpect(header().string("correlation-id", Matchers.not(Matchers.emptyString())));
    }
}
