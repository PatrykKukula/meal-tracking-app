package io.github.patrykkukula.diet_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.diet_ms.builder.ProductQuantityDtoTestBuilder;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDtoUpdate;
import io.github.patrykkukula.diet_ms.exception.ProductQuantityNotFoundException;
import io.github.patrykkukula.diet_ms.security.SecurityConfig;
import io.github.patrykkukula.diet_ms.service.ProductQuantityService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductQuantityController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class ProductQuantityControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private ProductQuantityService productQuantityService;

    private ProductQuantityDto productQuantityDto;
    private ProductQuantityDtoUpdate productQuantityDtoUpdate = new ProductQuantityDtoUpdate();
    private JwtRequestPostProcessor jwtAdmin;
    private JwtRequestPostProcessor jwtUser;
    private static final String BASER_URL = "/api/diets/quantity";

    @BeforeEach
    public void setUp() {
        productQuantityDto = ProductQuantityDtoTestBuilder
                .productQuantityDto().build();
        productQuantityDtoUpdate.setQuantity(999L);
        jwtAdmin = jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
        jwtUser = jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"));
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
                mockMvc.perform(delete(BASER_URL + "/{id}", 1)
                                .with(jwtAdmin))
                        .andExpect(status().isNoContent());

                verify(productQuantityService, times(1)).removeProductQuantity(anyLong());
            }
        }

        @Nested
        @DisplayName("when make request as USER")
        class whenMakeRequestAsUser {
            @Test
            @DisplayName("should allow authenticated endpoints")
            public void shouldAllowAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete(BASER_URL + "/{id}", 1)
                                .with(jwtUser))
                        .andExpect(status().isNoContent());

                verify(productQuantityService, times(1)).removeProductQuantity(anyLong());
            }
        }

        @Nested
        @DisplayName("when make request as anonymous")
        class whenMakeRequestAsAnonymous {
            @Test
            @DisplayName("should respond 401 when enter authenticated endpoints")
            public void shouldRespond403WhenEnterAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete(BASER_URL + "/{id}", 1))
                        .andExpectAll(status().isUnauthorized(),
                                jsonPath("$.statusMessage").value(HttpStatus.UNAUTHORIZED.getReasonPhrase()),
                                jsonPath("$.statusCode").value(401)
                        );

                verifyNoInteractions(productQuantityService);
            }
        }

        @Nested
        @DisplayName("when make request with unknown role")
        class whenMakeRequestWithUnknownRole {
            @Test
            @DisplayName("should respond 403 when enter authenticated endpoints")
            public void shouldRespond403WhenEnterAuthenticatedEndpoints() throws Exception {
                mockMvc.perform(delete(BASER_URL + "/{id}", 1)
                                .with(jwt().authorities(new SimpleGrantedAuthority("UNKNOWN_ROLE"))))
                        .andExpectAll(status().isForbidden(),
                                jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                                jsonPath("$.statusCode").value(403)
                        );

                verifyNoInteractions(productQuantityService);
            }
        }
    }

    @Nested
    @DisplayName("validation tests")
    class validationTests {
        @Test
        @DisplayName("should respond 400 when path variable ID is negative")
        public void shouldRespond400WhenPathVariableIdIsNegative() throws Exception {
            mockMvc.perform(delete(BASER_URL + "/{id}", -1)
                            .with(jwtAdmin))
                    .andExpectAll(status().isBadRequest(),
                            jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                            jsonPath("$.statusCode").value(400),
                            jsonPath("$.message").value(Matchers.containsString("Id cannot be less than 0"))
                    );

            verifyNoInteractions(productQuantityService);
        }

        @Test
        @DisplayName("should respond 400 when ProductQuantityDtoUpdate request body is invalid")
        public void shouldRespond400WhenProductQuantityDtoUpdateRequestBodyIsInvalid() throws Exception {
            productQuantityDtoUpdate.setQuantity(-1L);
            mockMvc.perform(patch(BASER_URL + "/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(productQuantityDtoUpdate))
                            .with(jwtAdmin))
                    .andExpectAll(status().isBadRequest(),
                            jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                            jsonPath("$.statusCode").value(400),
                            jsonPath("$.message").value(Matchers.containsString("Product quantity must be greater than 0"))
                    );

            verifyNoInteractions(productQuantityService);
        }
    }

    @Nested
    @DisplayName("when removeProductQuantity")
    class whenRemoveProductQuantity {
        @Test
        @DisplayName("should removeProductQuantity correctly")
        public void shouldRemoveProductQuantityCorrectly() throws Exception {
            doNothing().when(productQuantityService).removeProductQuantity(anyLong());

            mockMvc.perform(delete(BASER_URL + "/{id}", 1)
                            .with(jwtAdmin))
                    .andExpect(status().isNoContent());

            verify(productQuantityService, times(1)).removeProductQuantity(anyLong());
        }

        @Test
        @DisplayName("should respond 404 when ProductQuantityNotFoundException is thrown")
        public void shouldRespond404WhenProductQuantityNotFoundExceptionIsThrown() throws Exception {
            doThrow(new ProductQuantityNotFoundException(1L)).when(productQuantityService).removeProductQuantity(anyLong());

            mockMvc.perform(delete(BASER_URL + "/{id}", 1)
                            .with(jwtAdmin))
                    .andExpectAll(status().isNotFound(),
                            jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                            jsonPath("$.statusCode").value(404),
                            jsonPath("$.message").value(Matchers.containsString("ProductQuantity with ID 1 not found"))
                    );

            verify(productQuantityService, times(1)).removeProductQuantity(anyLong());
        }

        @Test
        @DisplayName("should respond 403 when AccessDeniedException is thrown")
        public void shouldRespond400WhenAccessDeniedExceptionIsThrown() throws Exception {
            doThrow(new AccessDeniedException("")).when(productQuantityService).removeProductQuantity(anyLong());

            mockMvc.perform(delete(BASER_URL + "/{id}", 1)
                            .with(jwtAdmin))
                    .andExpectAll(status().isForbidden(),
                            jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                            jsonPath("$.statusCode").value(403)
                    );

            verify(productQuantityService, times(1)).removeProductQuantity(anyLong());
        }

        @Nested
        @DisplayName("when updateProductQuantity")
        class whenUpdateProductQuantity {
            @Test
            @DisplayName("should updateProductQuantity correctly")
            public void shouldUpdateProductQuantityCorrectly() throws Exception {
                when(productQuantityService.updateProductQuantity(anyLong(), any(ProductQuantityDtoUpdate.class))).thenReturn(productQuantityDto);

                mockMvc.perform(patch(BASER_URL + "/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productQuantityDtoUpdate))
                                .with(jwtAdmin))
                        .andExpectAll(status().isAccepted(),
                                jsonPath("$.productId").value(1L),
                                jsonPath("$.quantity").value(5.0)
                        );

                verify(productQuantityService, times(1)).updateProductQuantity(anyLong(), any(ProductQuantityDtoUpdate.class));
            }

            @Test
            @DisplayName("should respond 404 when ProductQuantityNotFoundException is thrown")
            public void shouldRespond404WhenProductQuantityNotFoundExceptionIsThrown() throws Exception {
                doThrow(new ProductQuantityNotFoundException(1L)).when(productQuantityService).updateProductQuantity(anyLong(), any(ProductQuantityDtoUpdate.class));

                mockMvc.perform(patch(BASER_URL + "/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productQuantityDtoUpdate))
                                .with(jwtAdmin))
                        .andExpectAll(status().isNotFound(),
                                jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                                jsonPath("$.statusCode").value(404),
                                jsonPath("$.message").value(Matchers.containsString("ProductQuantity with ID 1 not found"))
                        );

                verify(productQuantityService, times(1)).updateProductQuantity(anyLong(), any(ProductQuantityDtoUpdate.class));
            }

            @Test
            @DisplayName("should respond 403 when AccessDeniedException is thrown")
            public void shouldRespond400WhenAccessDeniedExceptionIsThrown() throws Exception {
                doThrow(new AccessDeniedException("")).when(productQuantityService).updateProductQuantity(anyLong(), any(ProductQuantityDtoUpdate.class));

                mockMvc.perform(patch(BASER_URL + "/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(productQuantityDtoUpdate))
                                .with(jwtAdmin))
                        .andExpectAll(status().isForbidden(),
                                jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                                jsonPath("$.statusCode").value(403)
                        );

                verify(productQuantityService, times(1)).updateProductQuantity(anyLong(), any(ProductQuantityDtoUpdate.class));
            }
        }
    }
}
