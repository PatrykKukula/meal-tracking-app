package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.assembler.DietDayAssembler;
import io.github.patrykkukula.diet_ms.builder.DietDayTestBuilder;
import io.github.patrykkukula.diet_ms.builder.ProductQuantityDtoTestBuilder;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.exception.MealNotFoundException;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.repository.MealRepository;
import io.github.patrykkukula.diet_ms.security.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class    MealServiceUnitTest {
    @Mock
    private MealRepository mealRepository;
    @Mock
    private AuthenticationUtils authenticationUtils;
    @Mock
    private DietDayAssembler dietDayAssembler;
    @InjectMocks
    private MealService mealService;

    private ProductQuantityDto dto;
    private Meal meal = new Meal();
    private DietDay dietDay;

    @BeforeEach
    public void setUp() {
        dto = ProductQuantityDtoTestBuilder.productQuantityDto()
                        .quantity(2.0)
                        .build();
        dietDay = DietDayTestBuilder.dietDay()
                .owner("user")
                .build();

        meal.setDietDay(dietDay);

        Set<Meal> meals = new HashSet<>();

        meals.add(meal);
        dietDay.setMeals(meals);
    }

    @Nested
    @DisplayName("when removeMeal")
    class whenRemoveMeal {
        @Test
        @DisplayName("should remove Meal correctly")
        public void shouldRemoveMealCorrectly() {
            when(mealRepository.findById(anyLong())).thenReturn(Optional.of(meal));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");

            mealService.removeMeal(1L);

            assertEquals(0, dietDay.getMeals().size());
        }

        @Test
        @DisplayName("should throw MealNotFoundException when Meal not found")
        public void shouldThrowMealNotFoundExceptionWhenNotFound() {
            when(mealRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(MealNotFoundException.class, () -> mealService.removeMeal(1L));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when User is not owner")
        public void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
            when(mealRepository.findById(anyLong())).thenReturn(Optional.of(meal));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("bad");

            assertThrows(AccessDeniedException.class, () -> mealService.removeMeal(1L));
        }
    }

    @Nested
    @DisplayName("when addProductQuantity")
    class whenAddProductQuantity {
        @Test
        @DisplayName("should call DietDayAssembler correctly")
        public void shouldCallDietDayAssemblerCorrectly() {
            when(mealRepository.findById(anyLong())).thenReturn(Optional.of(meal));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
            when(dietDayAssembler.addProductQuantityToMeal(any(ProductQuantityDto.class), any(Meal.class), anyString())).thenReturn(dto);

            ProductQuantityDto returned = mealService.addProductQuantityToMeal(1L, dto);

            verify(dietDayAssembler, times(1)).addProductQuantityToMeal(eq(dto), eq(meal), eq("user"));
            assertEquals(2.0, returned.getQuantity());
            assertEquals(1L, returned.getProductId());
        }

        @Test
        @DisplayName("should throw MealNotFoundException when Meal not found")
        public void shouldThrowMealNotFoundExceptionWhenMealNotFound() {
            when(mealRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(MealNotFoundException.class, () -> mealService.removeMeal(1L));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when User is not owner")
        public void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
            when(mealRepository.findById(anyLong())).thenReturn(Optional.of(meal));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("bad");

            assertThrows(AccessDeniedException.class, () -> mealService.removeMeal(1L));
        }
    }
}
