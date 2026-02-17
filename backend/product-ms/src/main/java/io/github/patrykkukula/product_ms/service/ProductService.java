package io.github.patrykkukula.product_ms.service;

import io.github.patrykkukula.product_ms.constants.ProductCategory;
import io.github.patrykkukula.product_ms.dto.ProductDto;
import io.github.patrykkukula.product_ms.exception.CustomProductAmountExceededException;
import io.github.patrykkukula.product_ms.exception.ProductNotFoundException;
import io.github.patrykkukula.product_ms.mapper.ProductMapper;
import io.github.patrykkukula.product_ms.model.Product;
import io.github.patrykkukula.product_ms.repository.ProductRepository;
import io.github.patrykkukula.product_ms.security.AuthenticationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final AuthenticationUtils authenticationUtils;
    private final ProductRepository productRepository;
    private final StreamBridge streamBridge;
    private final int PAGE_SIZE = 50;
    private final int MAX_CUSTOM_PRODUCTS = 100;

    /**
     * Globally available products can only be added by ADMIN
     *
     * @param productDto - product details
     * @return added product info
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto addProduct(ProductDto productDto) {
        Product product = ProductMapper.mapProductDtoToProduct(productDto);

        Product savedProduct = productRepository.save(product);

        ProductDto savedDto = ProductMapper.mapProductToProductDto(savedProduct);

        productCreatedEvent(savedDto);

        return savedDto;
    }

    /**
     * Authenticated users can add custom products just for their use
     *
     * @param productDto - product details
     * @return added product info
     */
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ProductDto addCustomProduct(ProductDto productDto) {
        Product product = ProductMapper.mapProductDtoToProduct(productDto);

        String username = authenticationUtils.getAuthenticatedUserUsername();

        if (!canUserAddCustomProduct(username)) {
            throw new CustomProductAmountExceededException();
        }

        product.setOwnerUsername(username);

        Product savedProduct = productRepository.save(product);

        ProductDto savedDto = ProductMapper.mapProductToProductDto(savedProduct);

        productCreatedEvent(productDto);

        return savedDto;
    }

    @Cacheable(value = "product", unless = "#result.ownerUsername != null")
    public ProductDto findProductById(Long productId) {
        Product product = fetchProductById(productId);

        if (product.getOwnerUsername() == null)
            return ProductMapper.mapProductToProductDto(product);                                       // anyone can fetch global products

        String username = authenticationUtils.getAuthenticatedUserUsername();

        if (!Objects.equals(username, product.getOwnerUsername())) {                                    // only product owner can fetch product he added
            throw new AccessDeniedException("You do not have access to this product");
        }

        return ProductMapper.mapProductToProductDto(product);
    }

    /**
     * @param pageNo   - page number
     * @param category - product category to filter for - by default all categories are searched
     * @param name     - product name to filter for - by default do not filter
     * @return product list for given parameters
     */
    public List<ProductDto> findProducts(int pageNo, ProductCategory category, String name) {
        if (pageNo < 0) {
            throw new IllegalArgumentException("Page number cannot be less than 0");
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, sort);

        String username;

        // if user is authenticated additionally search for his custom products, else only search for global products
        try {
            username = authenticationUtils.getAuthenticatedUserUsername();
            log.info("Invoked findProducts with authenticated user: {}", username);
        } catch (AccessDeniedException ex) {
            log.info("Invoked findProducts with no authentication. Username set to null");
            username = null;
        }

        return productRepository.searchProducts(name, category, username, pageable).getContent()
                .stream()
                .map(ProductMapper::mapProductToProductDto)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @CacheEvict(value = "product", key = "#productId")
    public ProductDto updateProduct(ProductDto productDto, Long productId) {
        Product product = fetchProductById(productId);

        Product updatedProduct;

        boolean allowed = authenticationUtils.canUserModifyProduct(product);

        updatedProduct = ProductMapper.mapProductDtoToProductUpdate(productDto, product);

        ProductDto savedDto = ProductMapper.mapProductToProductDto(updatedProduct);

        productUpdateEvent(savedDto);

        return savedDto;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @CacheEvict(value = "product", key = "#productId")
    public void deleteProduct(Long productId) {
        Product product = fetchProductById(productId);

        boolean allowed = authenticationUtils.canUserModifyProduct(product);

        productRepository.deleteById(productId);

        productDeletedEvent(productId);
    }

    private Product fetchProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void productCreatedEvent(ProductDto productDto) {
        log.info("Sending ProductCreated Event to product.created Exchange: {}", productDto);
        boolean result = streamBridge.send("productCreated-out-0", productDto);
        log.info("ProductCreated Event sent successfully: {}", result);
    }

    private void productUpdateEvent(ProductDto productDto) {
        log.info("Sending ProductUpdated Event to product.updated Exchange: {}", productDto);
        boolean result = streamBridge.send("productUpdated-out-0", productDto);
        log.info("ProductUpdated Event sent successfully: {}", result);
    }

    private void productDeletedEvent(Long productId) {
        log.info("Sending ProductDeleted Event to product.deleted Exchange: {}", productId);
        boolean result = streamBridge.send("productDeleted-out-0", productId);
        log.info("ProductDeleted Event sent successfully: {}", result);
    }

    // returns true if user has less than 100 custom products added
    private boolean canUserAddCustomProduct(String username){
        return productRepository.fetchCustomProductsAmountForUser(username) < MAX_CUSTOM_PRODUCTS;
    }
}
