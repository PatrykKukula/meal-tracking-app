package io.github.patrykkukula.product_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SystemIntegrationTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    private ProductDto productDto;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor adminJwt;

    @BeforeEach
    public void setUp() {
        adminJwt = SecurityMockMvcRequestPostProcessors
                .jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

        productDto = ProductDto.builder()
                .name("product1")
                .productCategory(ProductCategory.FISH)
                .calories(100)
                .protein(100)
                .carbs(100)
                .fat(100)
                .build();
    }

    @Test
    @DisplayName("Should add product and then find product by id")
    public void shouldAddProductAndThenFindProductById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto))
                        .with(adminJwt))
                .andExpectAll(
                        status().isCreated(),
                        header().string("Location", Matchers.not(Matchers.emptyString())),
                        jsonPath("$.name").value("product1"),
                        jsonPath("$.calories").value(100),
                        jsonPath("$.protein").value(100),
                        jsonPath("$.carbs").value(100),
                        jsonPath("$.fat").value(100)
                )
                .andReturn();

        String location = mvcResult.getResponse().getHeader("location");
        assertNotNull(location);

        mockMvc.perform(get("/api/products/{productId}", location.substring(location.length() - 1)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("product1"),
                        jsonPath("$.calories").value(100),
                        jsonPath("$.protein").value(100),
                        jsonPath("$.carbs").value(100),
                        jsonPath("$.fat").value(100)
                );
    }

    @Test
    @DisplayName("Should respond 401 when add custom product as anonymous")
    @WithAnonymousUser
    public void shouldRespond401WhenAddCustomProductAsAnonymous() throws Exception {
        mockMvc.perform(post("/api/products/custom")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.statusMessage").value("Unauthorized"),
                        jsonPath("$.statusCode").value(401),
                        jsonPath("$.message").isNotEmpty(),
                        jsonPath("$.path").isNotEmpty(),
                        jsonPath("$.occurrenceTime").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should respond 404 when find product by id and no product found")
    @WithAnonymousUser
    public void shouldRespond404WhenFindProductByIdAndNoProductFound() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", 1L))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.statusMessage").value("Not Found"),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message").value(Matchers.containsString("not found")),
                        jsonPath("$.path").isNotEmpty(),
                        jsonPath("$.occurrenceTime").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Should update product correctly")
    public void shouldUpdateProductCorrectly() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/products")
                        .with(adminJwt)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.name").value("product1")
                )
                .andReturn();

        String location = mvcResult.getResponse().getHeader("location");
        assertNotNull(location);

        productDto.setName("updated name");

        mockMvc.perform(put("/api/products/{productId}", location.substring(location.length() - 1))
                        .with(adminJwt)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productDto)))
                .andExpectAll(
                        status().isAccepted(),
                        jsonPath("$.name").value("updated name")
                );

        mockMvc.perform(get("/api/products/{productId}", location.substring(location.length() - 1)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.name").value("updated name")
                );
    }
}
