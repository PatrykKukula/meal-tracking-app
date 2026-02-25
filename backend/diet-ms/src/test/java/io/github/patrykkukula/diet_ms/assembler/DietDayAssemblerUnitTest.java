package io.github.patrykkukula.diet_ms.assembler;

import io.github.patrykkukula.diet_ms.builder.DietDayDtoTestBuilder;
import io.github.patrykkukula.diet_ms.builder.MealDtoTestBuilder;
import io.github.patrykkukula.diet_ms.builder.ProductQuantityDtoTestBuilder;
import io.github.patrykkukula.diet_ms.builder.ProductSnapshotTestBuilder;
import io.github.patrykkukula.diet_ms.dto.DietDayDto;
import io.github.patrykkukula.diet_ms.dto.MealDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.model.ProductSnapshot;
import io.github.patrykkukula.diet_ms.repository.ProductSnapshotRepository;
import io.github.patrykkukula.diet_ms.security.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DietDayAssemblerUnitTest {
    @Mock
    private ProductSnapshotRepository productSnapshotRepository;
    @Mock
    private AuthenticationUtils authenticationUtils;
    @InjectMocks
    private DietDayAssembler dietDayAssembler;

    private DietDayDto dietDayDto;
    private MealDto mealDto;
    private ProductQuantityDto productQuantityDto;
    private ProductSnapshot productSnapshot;

    @BeforeEach
    public void setUp() {
        productQuantityDto = ProductQuantityDtoTestBuilder.productQuantityDto()
                .quantity(2.0)
                .build();
        mealDto = MealDtoTestBuilder.meal()
                        .name("breakfast")
                        .quantities(List.of(productQuantityDto))
                        .build();
        dietDayDto = DietDayDtoTestBuilder.dietDayDto()
                        .meals(List.of(mealDto))
                        .build();
        productSnapshot = ProductSnapshotTestBuilder.productSnapshot().build();
    }

    @Test
    @DisplayName("should assemble DietDay correctly")
    public void shouldAssembleDietDayCorrectly() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productSnapshotRepository.findById(anyLong())).thenReturn(Optional.of(productSnapshot));

        DietDay assembled = dietDayAssembler.assemble(dietDayDto);

        assertEquals("user", assembled.getOwnerUsername());
        assertEquals(1, assembled.getMeals().size());
        verify(productSnapshotRepository, times(1)).findById(eq(1L));
    }

    @Test
    @DisplayName("should create Meal correctly when assemble diet day")
    public void shouldCreateMealCorrectlyWhenAssembleDietDay() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productSnapshotRepository.findById(anyLong())).thenReturn(Optional.of(productSnapshot));

        DietDay assembled = dietDayAssembler.assemble(dietDayDto);
        assembled.getMeals().forEach(meal -> {
            assertEquals(0, meal.getOrderIndex());
            assertEquals(1, meal.getProductQuantities().size());
            assertEquals((LocalDate.of(2000, 1, 1)), meal.getDietDay().getDate());
        });
    }

    @Test
    @DisplayName("should create ProductQuantity correctly when assemble diet day")
    public void shouldCreateProductQuantityCorrectlyWhenAssembleDietDay() {
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productSnapshotRepository.findById(anyLong())).thenReturn(Optional.of(productSnapshot));

        DietDay assembled = dietDayAssembler.assemble(dietDayDto);
        assembled.getMeals().forEach(meal -> {
            List<ProductQuantity> quantities = meal.getProductQuantities();
            assertEquals(1, quantities.size());
            assertEquals("snapshot", quantities.getFirst().getProductSnapshot().getName());
        });
    }

    @Test
    @DisplayName("should set Meal name correctly when assemble diet day with no meal proviced")
    public void shouldSetMealNameCorrectlyWhenAssembleDietDayWithNoMealNameProvided() {
        mealDto.setName(null);
        when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");
        when(productSnapshotRepository.findById(anyLong())).thenReturn(Optional.of(productSnapshot));

        DietDay assembled = dietDayAssembler.assemble(dietDayDto);
        assembled.getMeals().forEach(meal -> assertEquals("Meal", meal.getName()));
    }
}
