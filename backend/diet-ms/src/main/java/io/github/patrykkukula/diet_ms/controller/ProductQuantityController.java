package io.github.patrykkukula.diet_ms.controller;

import io.github.patrykkukula.diet_ms.dto.ProductQuantityDto;
import io.github.patrykkukula.diet_ms.dto.ProductQuantityDtoUpdate;
import io.github.patrykkukula.diet_ms.service.ProductQuantityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/diets/quantity")
@RequiredArgsConstructor
@Validated
public class ProductQuantityController {
    private final ProductQuantityService productQuantityService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeProductQuantity(@PositiveOrZero(message = "Id cannot be less than 0") @PathVariable Long id){
        productQuantityService.removeProductQuantity(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductQuantityDto> updateProductQuantity(@PositiveOrZero(message = "Id cannot be less than 0") @PathVariable Long id,
                                                                    @Valid @RequestBody ProductQuantityDtoUpdate productQuantityDto) {
        ProductQuantityDto updatedQuantity = productQuantityService.updateProductQuantity(id, productQuantityDto);

        return ResponseEntity.accepted().body(updatedQuantity);
    }
}
