package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.assembler.DietDayAssembler;
import io.github.patrykkukula.diet_ms.builder.*;
import io.github.patrykkukula.diet_ms.constants.ProductCategory;
import io.github.patrykkukula.diet_ms.dto.*;
import io.github.patrykkukula.diet_ms.exception.DietDayNotFoundException;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.repository.DietDayRepository;
import io.github.patrykkukula.mealtrackingapp_common.security.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class    DietDayServiceUnitTest {
    @Mock
    private DietDayRepository dietDayRepository;
    @Mock
    private ProductSnapshotService productSnapshotService;
    @Mock
    private DietDayAssembler dietDayAssembler;
    @Mock
    private AuthenticationUtils authenticationUtils;
    @InjectMocks
    private DietDayService dietDayService;

    private DietDayDto dietDayDto;
    private MealDto mealDto;
    private ProductQuantityDto productQuantityDto;
    private ProductDtoRead productDtoRead;
    private DietDay dietDay;
    private Meal meal;

    @BeforeEach
    public void setUp() {
        productQuantityDto = ProductQuantityDtoTestBuilder.productQuantityDto()
                        .quantity(2.0)
                        .build();
        mealDto = MealDtoTestBuilder.meal()
                        .name("dinner")
                        .quantities(List.of(productQuantityDto))
                        .build();
        dietDayDto = DietDayDtoTestBuilder.dietDayDto()
                        .meals(List.of(mealDto))
                        .build();
        meal = MealTestBuilder.meal()
                        .name("breakfast")
                        .quantities(List.of())
                        .build();
        dietDay = DietDayTestBuilder.dietDay()
                        .owner("user")
                        .meals(Set.of(meal))
                        .build();
        productDtoRead = new ProductDtoRead(1L, 1L, 1.0, "product", ProductCategory.CEREAL,
                1, 1, 1, 1);
    }

    @Nested
    @DisplayName("When createDietDay")
    class whenCreateDietDay {
        @Test
        @DisplayName("should call beans correctly")
        public void shouldCallBeansCorrectly() {
            when(dietDayAssembler.assemble(any(DietDayDto.class))).thenReturn(dietDay);
            when(productSnapshotService.getProductsForMeal(any(Meal.class))).thenReturn(List.of(productDtoRead));
            when(dietDayRepository.save(any(DietDay.class))).thenReturn(dietDay);

            DietDayDtoRead createdDietDay = dietDayService.createDietDay(dietDayDto);

            verify(dietDayAssembler, times(1)).assemble(any());
            verify(productSnapshotService, times(1)).getProductsForMeal(meal);
            verify(dietDayRepository, times(1)).save(dietDay);
        }

        @Test
        @DisplayName("should return DietDayDtoRead correctly")
        public void shouldReturnDietDayDtoReadCorrectly() {
            when(dietDayAssembler.assemble(any(DietDayDto.class))).thenReturn(dietDay);
            when(productSnapshotService.getProductsForMeal(any(Meal.class))).thenReturn(List.of(productDtoRead));
            when(dietDayRepository.save(any(DietDay.class))).thenReturn(dietDay);

            DietDayDtoRead createdDietDay = dietDayService.createDietDay(dietDayDto);

            assertEquals(1L, createdDietDay.dietDayId());
            assertEquals("user", createdDietDay.ownerUsername());
            assertEquals(1, createdDietDay.meals().size());
            assertEquals("breakfast", createdDietDay.meals().getFirst().name());
            assertEquals(1, createdDietDay.meals().getFirst().products().size());
        }
    }

    @Nested
    @DisplayName("when getDietDayById")
    class whenGetDietDayById {
        @Test
        @DisplayName("should return DietDay correctly")
        public void shouldReturnDietDayCorrectly() {
            when(dietDayRepository.fetchDietDay(anyLong())).thenReturn(Optional.of(dietDay));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
            when(productSnapshotService.getProductsForMeal(any(Meal.class))).thenReturn(List.of(productDtoRead));

            DietDayDtoRead day = dietDayService.getDietDayById(1L);

            assertEquals("user", day.ownerUsername());
            assertEquals(1, day.meals().size());
            assertEquals(1L, day.dietDayId());
        }

        @Test
        @DisplayName("should throw DietDayNotFoundException when DietDay not found")
        public void shouldThrowDietDayNotFoundExceptionWhenDietDayNotFound() {
            when(dietDayRepository.fetchDietDay(anyLong())).thenReturn(Optional.empty());

            assertThrows(DietDayNotFoundException.class, () -> dietDayService.getDietDayById(1L));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when User is not owner")
        public void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
            when(dietDayRepository.fetchDietDay(anyLong())).thenReturn(Optional.of(dietDay));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("bad");

            assertThrows(AccessDeniedException.class, () -> dietDayService.getDietDayById(1L));
        }
    }

    @Nested
    @DisplayName("when removeDietDay")
    class whenRemoveDietDay {
        @Test
        @DisplayName("should removeDietDay correctly")
        public void shouldRemoveDietDayCorrectly() {
            when(dietDayRepository.fetchDietDay(anyLong())).thenReturn(Optional.of(dietDay));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
            doNothing().when(dietDayRepository).delete(any(DietDay.class));

            dietDayService.removeDietDay(1L);

            verify(dietDayRepository, times(1)).delete(eq(dietDay));
        }

        @Test
        @DisplayName("should throw DietDayNotFoundException when DietDay not found")
        public void shouldThrowDietDayNotFoundExceptionWhenDietDayNotFound() {
            when(dietDayRepository.fetchDietDay(anyLong())).thenReturn(Optional.empty());

            assertThrows(DietDayNotFoundException.class, () -> dietDayService.removeDietDay(1L));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when User is not owner")
        void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
            when(dietDayRepository.fetchDietDay(anyLong())).thenReturn(Optional.of(dietDay));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("bad");

            assertThrows(AccessDeniedException.class, () -> dietDayService.removeDietDay(1L));
        }
    }

    @Nested
    @DisplayName("when addMealToDietDay")
    class whenAddMealToDietDay {
        @Test
        @DisplayName("should return added Meal correctly")
        public void shouldReturnAddedMealCorrectly() {
            when(dietDayRepository.findById(anyLong())).thenReturn(Optional.of(dietDay));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
            when(dietDayAssembler.addMealToDietDay(any(MealDto.class), any(DietDay.class), anyString())).thenReturn(mealDto);

            MealDto addedMeal = dietDayService.addMealToDietDay(1L, mealDto);

            verify(dietDayAssembler, times(1)).addMealToDietDay(eq(mealDto), eq(dietDay), eq("user"));
            assertEquals("dinner", addedMeal.getName());
            assertEquals(1, addedMeal.getQuantities().size());
        }
    }
}
