package io.github.patrykkukula.diet_ms.service;

import io.github.patrykkukula.diet_ms.cache.CacheUtils;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDtoUpdate;
import io.github.patrykkukula.diet_ms.exception.ProductQuantityNotFoundException;
import io.github.patrykkukula.diet_ms.mapper.ProductQuantityMapper;
import io.github.patrykkukula.diet_ms.model.DietDay;
import io.github.patrykkukula.diet_ms.model.Meal;
import io.github.patrykkukula.diet_ms.model.ProductQuantity;
import io.github.patrykkukula.diet_ms.repository.ProductQuantityRepository;
import io.github.patrykkukula.diet_ms.security.AuthenticationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductQuantityService {
    private final ProductQuantityRepository productQuantityRepository;
    private final AuthenticationUtils authenticationUtils;
    private final CacheUtils cacheUtils;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void removeProductQuantity(Long quantityId) {
        ProductQuantity productQuantity = fetchProductQuantity(quantityId);

        isResourceOwner(productQuantity);

        Meal meal = productQuantity.getMeal();
        DietDay dietDay = meal.getDietDay();

        meal.removeProductQuantity(productQuantity);

        cacheUtils.evictCaches(dietDay);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ProductQuantityDto updateProductQuantity(Long quantityId, ProductQuantityDtoUpdate productQuantityDto) {
        ProductQuantity productQuantity = fetchProductQuantity(quantityId);

        isResourceOwner(productQuantity);

        productQuantity.setQuantity(productQuantityDto.getQuantity());

        DietDay dietDay = productQuantity.getMeal().getDietDay();
        cacheUtils.evictCaches(dietDay);

        return ProductQuantityMapper.mapProductQuantityToProductQuantityDto(productQuantity);
    }

    private ProductQuantity fetchProductQuantity(Long quantityId) {
        return productQuantityRepository.findById(quantityId).orElseThrow(() -> new ProductQuantityNotFoundException(quantityId));
    }

    // check if ProductQuantity belongs to user
    private void isResourceOwner(ProductQuantity productQuantity) {
        String username = authenticationUtils.getAuthenticatedUserUsername();

        if (!productQuantity.getMeal().getDietDay().getOwnerUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
