package io.github.patrykkukula.diet_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.diet_ms.builder.ProductQuantityDtoTestBuilder;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.exception.MealNotFoundException;
import io.github.patrykkukula.diet_ms.security.SecurityConfig;
import io.github.patrykkukula.diet_ms.service.MealService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MealController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MealControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private MealService mealService;

    private ProductQuantityDto productQuantityDto;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAdmin;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser;
    private static final String BASE_URL = "/api/diets/meal";

    @BeforeEach
    public void setUp() {
        productQuantityDto = ProductQuantityDtoTestBuilder.productQuantityDto().build();
        jwtAdmin = jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

        jwtUser = jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Nested
    @DisplayName("security tests")
    class securityTests {
        @Nested
        @DisplayName("when make request as ADMIN")
        class whenMakeRequestAsAdmin {

            @Test
            @DisplayName("should allow authenticated endpoints")
            public void shouldAllowAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .with(jwtAdmin))
                        .andExpect(status().isNoContent());

                verify(mealService, times(1)).removeMeal(anyLong());
            }
        }
        @Nested
        @DisplayName("when make request as USER")
        class whenMakeRequestAsUser {

            @Test
            @DisplayName("should allow authenticated endpoints")
            public void shouldAllowAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .with(jwtUser))
                        .andExpect(status().isNoContent());

                verify(mealService, times(1)).removeMeal(anyLong());
            }
        }
        @Nested
        @DisplayName("when make request as ANONYMOUS")
        class whenMakeRequestAsAnonymous {

            @Test
            @DisplayName("should respond 403 when enter authenticated endpoints")
            public void shouldDenyAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete(BASE_URL + "/{id}", 1L))
                        .andExpect(status().isUnauthorized());

                verifyNoInteractions(mealService);
            }
        }

        @Nested
        @DisplayName("when make request with unknown role")
        class whenMakeRequestWithUnknownRole {

            @Test
            @DisplayName("should respond 403 when enter authenticated endpoints")
            public void shouldDenyAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .with(jwt().authorities(new SimpleGrantedAuthority("UNKNOWN_ROLE"))))
                        .andExpect(status().isForbidden());

                verifyNoInteractions(mealService);
            }
        }
    }

    @Nested
    @DisplayName("constraints validation tests")
    class constraintsValidationTests {
        @Test
        @DisplayName("Should respond 400 when path variable ID is negative")
        public void shouldRespond400WhenPathVariableIdIsNegative() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/{id}", -1)
                            .with(jwtAdmin))
                    .andExpectAll(status().isBadRequest(),
                            jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                            jsonPath("$.statusCode").value(400),
                            jsonPath("$.message").value(Matchers.containsString("Id cannot be less than 0"))
                    );

            verifyNoInteractions(mealService);
        }

        @Test
        @DisplayName("Should respond 400 when ProductQuantityDto request body is invalid")
        public void shouldRespond400WhenProductQuantityDtoRequestBodyIsInvalid() throws Exception {
            productQuantityDto = new ProductQuantityDto();

            mockMvc.perform(post(BASE_URL + "/{id}" + "/add_quantity", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(productQuantityDto))
                    .with(jwtAdmin))
                    .andExpectAll(status().isBadRequest(),
                            jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                            jsonPath("$.statusCode").value(400),
                            jsonPath("$.message").value(Matchers.containsString("ProductId cannot be empty")),
                            jsonPath("$.message").value(Matchers.containsString("Specify product quantity"))
                    );

            verifyNoInteractions(mealService);
        }
    }

    @Nested
    @DisplayName("when removeMeal")
    class whenRemoveMeal {
        @Test
        @DisplayName("should remove meal correctly")
        public void shouldRemoveMealCorrectly() throws Exception {
            doNothing().when(mealService).removeMeal(anyLong());

            mockMvc.perform(delete(BASE_URL + "/{id}", 1)
                            .with(jwtAdmin))
                    .andExpect(status().isNoContent());

            verify(mealService, times(1)).removeMeal(anyLong());
        }

        @Test
        @DisplayName("should respond 404 when MealNotFoundException is thrown")
        public void shouldRespond404WhenMealNotFoundExceptionIsThrown() throws Exception {
            doThrow(new MealNotFoundException(1L)).when(mealService).removeMeal(anyLong());

            mockMvc.perform(delete(BASE_URL + "/{id}", 1)
                            .with(jwtAdmin))
                    .andExpectAll(status().isNotFound(),
                            jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                            jsonPath("$.statusCode").value(404),
                            jsonPath("$.message").value(Matchers.containsString("Meal with ID 1 not found")));

            verify(mealService, times(1)).removeMeal(anyLong());
        }

        @Test
        @DisplayName("should respond 404 when AccessDeniedExceptionIsThrown")
        public void shouldRespond403WhenAccessDeniedExceptionIsThrown() throws Exception {
            doThrow(new AccessDeniedException("")).when(mealService).removeMeal(anyLong());

            mockMvc.perform(delete(BASE_URL + "/{id}", 1)
                            .with(jwtAdmin))
                    .andExpectAll(status().isForbidden(),
                            jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                            jsonPath("$.statusCode").value(403)
                    );

            verify(mealService, times(1)).removeMeal(anyLong());
        }
    }
    @Nested
    @DisplayName("when AddProductQuantityToMeal")
    class whenAddProductQuantityToMeal {
        @Test
        @DisplayName("should add product quantity to meal correctly")
        public void shouldAddProductQuantityToMealCorrectly() throws Exception {
            when(mealService.addProductQuantityToMeal(anyLong(), any(ProductQuantityDto.class))).thenReturn(productQuantityDto);

            mockMvc.perform(post(BASE_URL + "/{id}" + "/add_quantity", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productQuantityDto))
                    .with(jwtAdmin))
                    .andExpectAll(status().isAccepted(),
                            jsonPath("$.productId").value(1),
                            jsonPath("$.quantity").value(5.0)
                    );

            verify(mealService, times(1)).addProductQuantityToMeal(anyLong(), any(ProductQuantityDto.class));
        }

        @Test
        @DisplayName("should respond 404 when MealNotFoundException is thrown")
        public void shouldRespond404WhenMealNotFoundExceptionIsThrown() throws Exception {
            when(mealService.addProductQuantityToMeal(anyLong(), any(ProductQuantityDto.class))).thenThrow(new MealNotFoundException(1L));

            mockMvc.perform(post(BASE_URL + "/{id}" + "/add_quantity", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(productQuantityDto))
                            .with(jwtAdmin))
                    .andExpectAll(status().isNotFound(),
                            jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                            jsonPath("$.statusCode").value(404),
                            jsonPath("$.message").value(Matchers.containsString("Meal with ID 1 not found")));

            verify(mealService, times(1)).addProductQuantityToMeal(anyLong(), any(ProductQuantityDto.class));
        }

        @Test
        @DisplayName("should respond 404 when AccessDeniedExceptionIsThrown")
        public void shouldRespond403WhenAccessDeniedExceptionIsThrown() throws Exception {
            when(mealService.addProductQuantityToMeal(anyLong(), any(ProductQuantityDto.class))).thenThrow(new AccessDeniedException(""));

            mockMvc.perform(post(BASE_URL + "/{id}" + "/add_quantity", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(productQuantityDto))
                            .with(jwtAdmin))
                    .andExpectAll(status().isForbidden(),
                            jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                            jsonPath("$.statusCode").value(403)
                    );

            verify(mealService, times(1)).addProductQuantityToMeal(anyLong(), any(ProductQuantityDto.class));
        }
    }
}
