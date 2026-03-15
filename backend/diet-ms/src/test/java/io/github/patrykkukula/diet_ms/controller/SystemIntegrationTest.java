package io.github.patrykkukula.diet_ms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.patrykkukula.diet_ms.builder.DietDayDtoTestBuilder;
import io.github.patrykkukula.diet_ms.builder.MealDtoTestBuilder;
import io.github.patrykkukula.diet_ms.builder.ProductQuantityDtoTestBuilder;
import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.repository.DietDayRepository;
import io.github.patrykkukula.diet_ms.repository.ProductQuantityRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SystemIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private DietDayRepository dietDayRepository;
    @Autowired
    private ProductQuantityRepository productQuantityRepository;

    private ProductQuantityDto productQuantityDto;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAdmin;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtUser;
    private DietDayDto dietDayDto;
    private MealDto mealDto;
    private static final String DIET_DAY_API_BASE_URL = "/api/diets";

    @BeforeEach
    public void setUp() {
        productQuantityDto = ProductQuantityDtoTestBuilder.productQuantityDto().build();
        mealDto = MealDtoTestBuilder.meal()
                .name("breakfast")
                .quantities(List.of(productQuantityDto))
                .build();
        dietDayDto = DietDayDtoTestBuilder.dietDayDto()
                .date(LocalDate.of(2100, 1, 1))
                .meals(List.of(mealDto))
                .build();
        jwtAdmin = jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .jwt(builder -> builder.claim("preferred_username", "owner"));
        jwtUser = jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .jwt(builder -> builder.claim("preferred_username", "user"));
    }

    @AfterEach
    public void clear() {
        dietDayRepository.deleteAll();
    }

    @Test
    @DisplayName("should createDietDay and then getDietDayById")
    public void shouldCreateDietDayAndThenGetDietDayById() throws Exception {
        assertTrue(dietDayRepository.findById(1L).isEmpty());

        MvcResult mvcResult = performCreateDietDay();

        String id = getCreatedDietDayId(mvcResult);

        mockMvc.perform(get(DIET_DAY_API_BASE_URL + "/{id}", id)
                .with(jwtAdmin))
                .andExpectAll(status().isOk(),
                        jsonPath("$.ownerUsername").value("owner"),
                        jsonPath("$.meals.size()").value(1)
                );
    }

    @Test
    @DisplayName("should respond 404 when getDietDayById after removeDietDayById")
    public void shouldRespond404WhenGetDietDayByIdAfterRemoveDietDayById() throws Exception {
        MvcResult mvcResult = performCreateDietDay();

        String id = getCreatedDietDayId(mvcResult);

        assertTrue(dietDayRepository.findById(Long.parseLong(id)).isPresent());

        mockMvc.perform(delete(DIET_DAY_API_BASE_URL + "/{id}", id)
                        .with(jwtAdmin))
                .andExpectAll(status().isNoContent());

        mockMvc.perform(get(DIET_DAY_API_BASE_URL + "/{id}", id)
                        .with(jwtAdmin))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.statusMessage").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                        jsonPath("$.statusCode").value(404),
                        jsonPath("$.message").value(("DietDay with ID %s not found".formatted(id)))
                );
    }

    @Test
    @DisplayName("should add meal to existing DietDay correctly")
    public void shouldAddMealToExistingDietDayCorrectly() throws Exception {
        MvcResult mvcResult = performCreateDietDay();

        String id = getCreatedDietDayId(mvcResult);

        mockMvc.perform(post(DIET_DAY_API_BASE_URL + "/{id}" + "/add_meal", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mealDto))
                        .with(jwtAdmin))
                .andExpect(status().isAccepted());

        mockMvc.perform(get(DIET_DAY_API_BASE_URL + "/{id}", id)
                        .with(jwtAdmin))
                .andExpectAll(status().isOk(),
                        jsonPath("$.meals.size()").value((2))
                );
    }

    @Test
    @DisplayName("should createDietDay and then respond 403 when getDietDayById as other user")
    public void shouldCreateDietDayAndThenRespond403WhenGetDietDayByIdAsOtherUser() throws Exception {
        MvcResult mvcResult = performCreateDietDay();

        String id = getCreatedDietDayId(mvcResult);

        mockMvc.perform(get(DIET_DAY_API_BASE_URL + "/{id}", id)
                        .with(jwtUser))
                .andExpectAll(status().isForbidden(),
                        jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                        jsonPath("$.statusCode").value(403),
                        jsonPath("$.message").value("Access denied")
                );
    }

    @Test
    @DisplayName("should createDietDay and then removeMealFromDietDayCorrectly")
    public void shouldCreateDietDayAndThenRemoveMealFromDietDayCorrectly() throws Exception {
        dietDayDto.setMeals(List.of(mealDto, mealDto));

        MvcResult mvcResult = performCreateDietDay();

        String id = getCreatedDietDayId(mvcResult);

        Optional<DietDay> dietDayOpt = dietDayRepository.findByIdWithMeals(Long.parseLong(id));
        assertTrue(dietDayOpt.isPresent());

        DietDay dietDay = dietDayOpt.get();
        assertEquals(2, dietDay.getMeals().size());

        Meal meal = dietDay.getMeals().stream()
                .toList()
                .getFirst();

        mockMvc.perform(delete(DIET_DAY_API_BASE_URL + "/meal/{id}", meal.getMealId())
                        .with(jwtAdmin))
                .andExpectAll(status().isNoContent()
                );

        dietDayRepository.findByIdWithMeals(Long.parseLong(id)).ifPresent(day -> assertEquals(1, day.getMeals().size()));
    }

    @Test
    @DisplayName("should createDietDay and then respond 400 when addProductQuantityToMeal with invalid ProductQuantityDto request body")
    public void shouldCreateDietDayAndThenRespond400WhenAddProductQuantityToMealWithInvalidProductQuantityRequestBody() throws Exception {
        MvcResult mvcResult = performCreateDietDay();

        String id = getCreatedDietDayId(mvcResult);

        Optional<DietDay> dietDayOpt = dietDayRepository.findByIdWithMeals(Long.parseLong(id));
        assertTrue(dietDayOpt.isPresent());

        DietDay dietDay = dietDayOpt.get();

        Meal meal = dietDay.getMeals().stream()
                .toList()
                .getFirst();

        productQuantityDto.setQuantity(-1L);

        mockMvc.perform(post(DIET_DAY_API_BASE_URL + "/meal/{id}/add_quantity", meal.getMealId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(productQuantityDto))
                        .with(jwtAdmin))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.statusMessage").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                        jsonPath("$.statusCode").value(400),
                        jsonPath("$.message").value(Matchers.containsString("Product quantity must be positive value"))
                );
    }

    @Test
    @DisplayName("should createDietDay and then respond 403 when removeProductQuantity as other user")
    public void shouldCreateDietDayAndThenRespond403WhenRemoveProductQuantityAsOtherUser() throws Exception {
        MvcResult mvcResult = performCreateDietDay();

        String id = getCreatedDietDayId(mvcResult);

        Optional<DietDay> dietDayOpt = dietDayRepository.findByIdWithMeals(Long.parseLong(id));
        assertTrue(dietDayOpt.isPresent());

        DietDay dietDay = dietDayOpt.get();

        Meal meal = dietDay.getMeals().stream()
                .toList()
                .getFirst();

        List<ProductQuantity> quantities = productQuantityRepository.getProductQuantitiesForMeal(meal.getMealId());

        mockMvc.perform(delete(DIET_DAY_API_BASE_URL + "/quantity/{id}", quantities.getFirst().getProductQuantityId())
                        .with(jwtUser))
                .andExpectAll(status().isForbidden(),
                        jsonPath("$.statusMessage").value(HttpStatus.FORBIDDEN.getReasonPhrase()),
                        jsonPath("$.statusCode").value(403),
                        jsonPath("$.message").value(Matchers.containsString("Access denied"))
                );
    }

    private MvcResult performCreateDietDay() throws Exception {
        return mockMvc.perform(post(DIET_DAY_API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dietDayDto))
                        .with(jwtAdmin))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private String getCreatedDietDayId(MvcResult mvcResult) {
        String location = mvcResult.getResponse().getHeader("location");
        assertNotNull(location);
        return location.substring(location.length() - 1);
    }
}
