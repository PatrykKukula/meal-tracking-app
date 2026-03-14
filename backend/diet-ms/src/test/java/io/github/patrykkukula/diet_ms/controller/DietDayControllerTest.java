package io.github.patrykkukula.diet_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.diet_ms.builder.DietDayDtoTestBuilder;
import io.github.patrykkukula.diet_ms.builder.MealDtoTestBuilder;
import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.DietDayDtoRead;
import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.exception.DietDayNotFoundException;
import io.github.patrykkukula.diet_ms.exception.ProductSnapshotNotFoundException;
import io.github.patrykkukula.diet_ms.service.DietDayService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DietDayController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DietDayControllerTest {
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ObjectMapper mapper;
    @MockitoBean
    public DietDayService dietDayService;

    private static final String BASE_URL = "/api/diets";
    private DietDayDto dietDayDto;
    private DietDayDtoRead dietDayDtoRead;
    private MealDto mealDto;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAdmin;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser;

    @BeforeEach
    public void setUp() {
        dietDayDto = DietDayDtoTestBuilder.dietDayDto()
                .date(LocalDate.of(2100, 1, 1))
                .meals(List.of(new MealDto()))
                .build();
        dietDayDtoRead = new DietDayDtoRead(1L, "user", LocalDate.now(), Collections.emptyList());
        mealDto = MealDtoTestBuilder.meal()
                .name("a".repeat(33))
                .build();
        jwtAdmin = SecurityMockMvcRequestPostProcessors
                .jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));

        jwtUser = SecurityMockMvcRequestPostProcessors
                .jwt()
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
                mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dietDayDto))
                        .with(jwtAdmin));

                verify(dietDayService, times(1)).createDietDay(any(DietDayDto.class));
            }
        }
        @Nested
        @DisplayName("when make request as USER")
        class whenMakeRequestAsUser {

            @Test
            @DisplayName("should allow authenticated endpoints")
            public void shouldAllowAuthenticatedEndpoints() throws Exception {
                when(dietDayService.createDietDay(any(DietDayDto.class))).thenReturn(dietDayDtoRead);

                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dietDayDto))
                                .with(jwtUser));

                verify(dietDayService, times(1)).createDietDay(any(DietDayDto.class));
            }
        }
            @Nested
            @DisplayName("when make request as ANONYMOUS")
            class whenMakeRequestAsAnonymous {

                @Test
                @DisplayName("should respond 403 when enter authenticated endpoints")
                public void shouldDenyAuthenticatedEndpoints() throws Exception {
                    mockMvc.perform(post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(mapper.writeValueAsString(dietDayDto)))
                            .andExpect(status().isForbidden());

                    verifyNoInteractions(dietDayService);
                }
            }

        @Nested
        @DisplayName("when make request with unknown role")
        class whenMakeRequestWithUnknownRole {

            @Test
            @DisplayName("should respond 403 when enter authenticated endpoints")
            public void shouldDenyAuthenticatedEndpoints() throws Exception {
                jwtAdmin.authorities(new SimpleGrantedAuthority("UNKNOWN_ROLE"));
                when(dietDayService.createDietDay(any(DietDayDto.class))).thenReturn(dietDayDtoRead);

                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dietDayDto)))
                        .andExpect(status().isForbidden());

                verifyNoInteractions(dietDayService);
            }
        }
    }
    @Nested
    @DisplayName("validation tests")
    class constraintsValidationsTests {

        @Test
        @DisplayName("should respond 400 when DietDayDto body is invalid")
        public void shouldValidateDietDayDtoBodyCorrectly() throws Exception {
            dietDayDto = DietDayDtoTestBuilder.dietDayDto().build();
            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dietDayDto))
                    .with(jwtAdmin))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                            jsonPath("$.statusCode").value(400),
                            jsonPath("$.message").value(Matchers.containsString("You cannot add diet to the past day")),
                            jsonPath("$.message").value(Matchers.containsString("Diet day must contains at least one meal"))
                    );

            verifyNoInteractions(dietDayService);
        }

        @Test
        @DisplayName("should respond 400 when MealDto body is invalid")
        public void shouldValidateMealDtoBodyCorrectly() throws Exception {
            dietDayDto = DietDayDtoTestBuilder.dietDayDto().build();
            mockMvc.perform(post(BASE_URL + "/{id}/add_meal", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(mealDto))
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                            jsonPath("$.statusCode").value(400),
                            jsonPath("$.message").value(Matchers.containsString("Meal name is too long!")),
                            jsonPath("$.message").value(Matchers.containsString("Meal must have at least one product"))
                    );

            verifyNoInteractions(dietDayService);
        }

        @Test
        @DisplayName("should respond 400 when path variable ID is negative")
        public void shouldRespond400WhenPathVariableIdIsNegative() throws Exception {
            mockMvc.perform(get(BASE_URL + "/{id}", -1L)
                    .with(jwtAdmin))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                            jsonPath("$.statusCode").value(400),
                            jsonPath("$.message").value(Matchers.containsString("Id cannot be less than 0"))
                    );

            verifyNoInteractions(dietDayService);
        }
    }
    @Nested
    @DisplayName("when createDietDay")
    class whenCreateDietDay {
        @Test
        @DisplayName("should create DietDay correctly")
        public void shouldCreateDietDayCorrectly() throws Exception {
            when(dietDayService.createDietDay(any(DietDayDto.class))).thenReturn(dietDayDtoRead);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dietDayDto))
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isCreated(),
                            header().exists("location"),
                            jsonPath("$.dietDayId").value(1L),
                            jsonPath("$.ownerUsername").value("user"),
                            jsonPath("$.meals.size()").value(0)
                    );

            verify(dietDayService, times(1)).createDietDay(any(DietDayDto.class));
        }

        @Test
        @DisplayName("should respond 404 when ProductSnapshotNotFoundException is thrown")
        public void shouldRespond404WhenProductSnapshotNotFoundExceptionIsThrown() throws Exception {
            when(dietDayService.createDietDay(any(DietDayDto.class))).thenThrow(new ProductSnapshotNotFoundException(1L));

            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dietDayDto))
                    .with(jwtAdmin))
                    .andExpectAll(
                      status().isNotFound(),
                            jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                            jsonPath("$.statusCode").value(404),
                            jsonPath("$.message").value(("Product snapshot with ID 1 not found"))
                    );

            verify(dietDayService, times(1)).createDietDay(any(DietDayDto.class));
        }
    }
    @Nested
    @DisplayName("when getDietDayById")
    class whenGetDietDayById {
        @Test
        @DisplayName("should return DietDayDtoRead correctly when getDietDayById")
        public void shouldReturnDietDayDtoReadCorrectly() throws Exception {
            when(dietDayService.getDietDayById(anyLong())).thenReturn(dietDayDtoRead);

            mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                    .with(jwtAdmin))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.dietDayId").value(1L),
                            jsonPath("$.ownerUsername").value("user"),
                            jsonPath("$.meals.size()").value(0)
                    );

            verify(dietDayService, times(1)).getDietDayById(1L);
        }

        @Test
        @DisplayName("should respond 404 when DietDayNotFoundException is thrown")
        public void shouldRespond404WhenDietDayNotFoundExceptionIsThrown() throws Exception {
            when(dietDayService.getDietDayById(anyLong())).thenThrow(new DietDayNotFoundException(1L));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                            jsonPath("$.statusCode").value(404),
                            jsonPath("$.message").value(("DietDay with ID 1 not found"))
                    );

            verify(dietDayService, times(1)).getDietDayById(1L);
        }

        @Test
        @DisplayName("should respond 403 when AccessDeniedException is throw")
        public void shouldRespond403WhenAccessDeniedExceptionIsThrow() throws Exception {
            when(dietDayService.getDietDayById(anyLong())).thenThrow(new AccessDeniedException("Access denied"));

            mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                            jsonPath("$.statusCode").value(403),
                            jsonPath("$.message").value(("Access denied"))
                    );

            verify(dietDayService, times(1)).getDietDayById(1L);
        }
    }
    @Nested
    @DisplayName("when removeDietDay")
    class whenRemoveDietDay {
        @Test
        @DisplayName("should removeDietDay correctly")
        public void shouldRemoveDietDayCorrectly() throws Exception {
            doNothing().when(dietDayService).removeDietDay(anyLong());

            mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                            .with(jwtAdmin))
                    .andExpect(status().isNoContent());

            verify(dietDayService, times(1)).removeDietDay(1L);
        }

        @Test
        @DisplayName("should respond 404 when DietDayNotFoundException is thrown")
        public void shouldRespond404WhenDietDayNotFoundExceptionIsThrown() throws Exception {
            doThrow(new DietDayNotFoundException(1L)).when(dietDayService).removeDietDay(anyLong());

            mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                            jsonPath("$.statusCode").value(404),
                            jsonPath("$.message").value(("DietDay with ID 1 not found"))
                    );

            verify(dietDayService, times(1)).removeDietDay(1L);
        }

        @Test
        @DisplayName("should respond 403 when AccessDeniedException is throw")
        public void shouldRespond403WhenAccessDeniedExceptionIsThrow() throws Exception {
            doThrow(new AccessDeniedException("Access denied")).when(dietDayService).removeDietDay(anyLong());

            mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                            jsonPath("$.statusCode").value(403),
                            jsonPath("$.message").value(("Access denied"))
                    );

            verify(dietDayService, times(1)).removeDietDay(1L);
        }
    }

    @Nested
    @DisplayName("when addMealToDietDay")
    class whenAddMealToDietDay {
        @Test
        @DisplayName("should addMealToDietDay correctly")
        public void shouldAddMealToDietDayCorrectly() throws Exception {
            mealDto.setName("meal");
            mealDto.setQuantities(List.of(new ProductQuantityDto()));
            when(dietDayService.addMealToDietDay(anyLong(), any(MealDto.class))).thenReturn(mealDto);

            mockMvc.perform(post(BASE_URL + "/{id}/add_meal", 1L)
                            .with(jwtAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(mealDto)))
                    .andExpectAll(
                            status().isAccepted(),
                            jsonPath("$.name").value("meal"),
                            jsonPath("$.quantities.size()").value(1)
                    );

            verify(dietDayService, times(1)).addMealToDietDay(anyLong(), any(MealDto.class));
        }

        @Test
        @DisplayName("should respond 404 when DietDayNotFoundException is thrown")
        public void shouldRespond404WhenDietDayNotFoundExceptionIsThrown() throws Exception {
            mealDto.setName("meal");
            mealDto.setQuantities(List.of(new ProductQuantityDto()));
            when(dietDayService.addMealToDietDay(anyLong(), any(MealDto.class))).thenThrow(new DietDayNotFoundException(1L));

            mockMvc.perform(post(BASE_URL + "/{id}/add_meal", 1L)
                            .content(mapper.writeValueAsString(mealDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                            jsonPath("$.statusCode").value(404),
                            jsonPath("$.message").value(("DietDay with ID 1 not found"))
                    );

            verify(dietDayService, times(1)).addMealToDietDay(anyLong(), any(MealDto.class));
        }

        @Test
        @DisplayName("should respond 403 when AccessDeniedException is throw")
        public void shouldRespond403WhenAccessDeniedExceptionIsThrow() throws Exception {
            mealDto.setName("meal");
            mealDto.setQuantities(List.of(new ProductQuantityDto()));
            when(dietDayService.addMealToDietDay(anyLong(), any(MealDto.class))).thenThrow(new AccessDeniedException("Access denied"));

            mockMvc.perform(post(BASE_URL + "/{id}/add_meal", 1L)
                            .content(mapper.writeValueAsString(mealDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(jwtAdmin))
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                            jsonPath("$.statusCode").value(403),
                            jsonPath("$.message").value(("Access denied"))
                    );

            verify(dietDayService, times(1)).addMealToDietDay(anyLong(), any(MealDto.class));
        }
    }
}

