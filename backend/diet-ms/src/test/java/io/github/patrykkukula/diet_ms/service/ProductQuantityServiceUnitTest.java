package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.builder.DietDayTestBuilder;
import io.github.patrykkukula.diet_ms.builder.MealTestBuilder;
import io.github.patrykkukula.diet_ms.builder.ProductQuantityTestBuilder;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDtoUpdate;
import io.github.patrykkukula.diet_ms.exception.ProductQuantityNotFoundException;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.repository.ProductQuantityRepository;
import io.github.patrykkukula.mealtrackingapp_common.security.AuthenticationUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductQuantityServiceUnitTest {
    @Mock
    private ProductQuantityRepository productQuantityRepository;
    @Mock
    private AuthenticationUtils authenticationUtils;
    @InjectMocks
    private ProductQuantityService productQuantityService;

    private ProductQuantity productQuantity;
    private Meal meal;
    private DietDay dietDay;
    private ProductQuantityDtoUpdate dto = new ProductQuantityDtoUpdate();

    @BeforeEach
    public void setUp(){
        dietDay = DietDayTestBuilder.dietDay()
                        .owner("user")
                        .build();
        meal = MealTestBuilder.meal()
                        .dietDay(dietDay)
                        .build();
        productQuantity = ProductQuantityTestBuilder.productQuantity()
                        .quantity(2.0)
                        .meal(meal)
                        .build();
        meal.getProductQuantities().add(productQuantity);
        dto.setQuantity(5.0);
    }

    @Nested
    @DisplayName("when removeProductQuantity")
    class whenRemoveProductQuantity {
        @Test
        @DisplayName("should remove ProductQuantity correctly")
        public void shouldRemoveProductQuantityCorrectly() {
            when(productQuantityRepository.findById(anyLong())).thenReturn(Optional.of(productQuantity));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");

            productQuantityService.removeProductQuantity(1L);

            assertEquals(0, meal.getProductQuantities().size());
        }

        @Test
        @DisplayName("should throw ProductQuantityNotFoundException when ProductQuantity not found")
        public void shouldThrowProductQuantityExceptionWhenProductQuantityNotFound() {
            when(productQuantityRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ProductQuantityNotFoundException.class, () -> productQuantityService.removeProductQuantity(1L));
        }
        @Test
        @DisplayName("should throw AccessDeniedException when User is not owner")
        public void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
            when(productQuantityRepository.findById(anyLong())).thenReturn(Optional.of(productQuantity));

            assertThrows(AccessDeniedException.class, () -> productQuantityService.removeProductQuantity(1L));
        }
    }

    @Nested
    @DisplayName("when updateProductQuantity")
    class whenUpdateProductQuantity {
        @Test
        @DisplayName("should update ProductQuantity correctly")
        public void shouldUpdateProductQuantityCorrectly() {
            when(productQuantityRepository.findById(anyLong())).thenReturn(Optional.of(productQuantity));
            when(authenticationUtils.getAuthenticatedUserUsername()).thenReturn("user");

            ProductQuantityDto updateProduct = productQuantityService.updateProductQuantity(1L, dto);

            assertEquals(5.0, updateProduct.getQuantity());
        }

        @Test
        @DisplayName("should throw ProductQuantityNotFoundException when ProductQuantity not found")
        public void shouldThrowProductQuantityExceptionWhenProductQuantityNotFound() {
            when(productQuantityRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ProductQuantityNotFoundException.class, () -> productQuantityService.updateProductQuantity(1L, dto));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when User is not owner")
        public void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
            when(productQuantityRepository.findById(anyLong())).thenReturn(Optional.of(productQuantity));

            assertThrows(AccessDeniedException.class, () -> productQuantityService.updateProductQuantity(1L, dto));
        }
    }
}
